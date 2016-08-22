package com.kodebeagle.javaparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.builder.DirectedGraphBuilder;

import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.InvocationNode;
import com.kb.java.graph.LabelNode;
import com.kb.java.graph.NamedDirectedGraph;
import com.kb.java.graph.Node;
import com.kb.java.graph.SeedCreationNode;
import com.kb.java.graph.SeedCreationNode.SeedCreationType;

public class CFGResolver extends MethodInvocationResolver {

	private int nodeId = 0;
	private String fileName = "";
	private Node currentNode;
	private Stack<ParseState> nodeStack = new Stack<ParseState>();
	private Stack<DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>>> gbStack = 
			new Stack<DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>>>();
	private List<NamedDirectedGraph> methodCFGs = new ArrayList<NamedDirectedGraph>();
	private Map<ASTNode, Node> nodeMap = new HashMap<ASTNode, Node>();
	private int minVertices = 2;
	private String seed = "";
	private static int graphId = 0;

	public CFGResolver() {
	}
	
	public CFGResolver(String seed, String fileName){
		this.seed = seed;
		this.fileName = fileName;
	}

	public CFGResolver(Integer minVertices) {
		this.minVertices = minVertices;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		DirectedGraph<Node, DirectedEdge> currentGraph = new DefaultDirectedGraph<Node, DirectedEdge>(DirectedEdge.class);
		DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>> graphBuilder = new DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>>(
				currentGraph);
		gbStack.push(graphBuilder);
		Node root = addRoot(node, graphBuilder);
		String seedVar = addParamNode(node, root);
		Boolean seeded = false; 
		if(StringUtils.isNotEmpty(seedVar)){
			//Param node is added
			seeded = true;
		}else {
			currentNode = root;
		}
		
		Set<String> paramTypes = new HashSet<String>();
		for(Object p : node.parameters()){
			if (p instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) p;
				Type type = svd.getType();
				String typeName = getNonParametericType(getNameOfType(type));
				paramTypes.add(typeName);
			}
		}
		
		nodeStack.push(new ParseState(currentNode, seeded, seedVar, node.getName().toString(), paramTypes));
		
		return super.visit(node);
	}
	
		
	@SuppressWarnings("unchecked")
	public boolean visit(VariableDeclarationStatement node){
		super.visit(node);
		
		if(nodeStack.isEmpty()){
			return true;
		}
		
		if(nodeStack.peek().seeded){
			//if already seeded use that seed
			return true;
		}
		
		Type type = node.getType();
		final String varType = getNameOfType(type);
		
		List<VariableDeclarationFragment> frags = node.fragments();

		Expression init = null;
		String varName = null;
		
		for(VariableDeclarationFragment frag : frags){
			frag.accept(this);
			init = frag.getInitializer();
			if(init == null){
				continue;
			}
			varName = frag.getName().getIdentifier();
			break;
		}
		
		String actualInstanceType = "";
		Node initNode = null;
		if(init instanceof ClassInstanceCreation){
			//Calling constructor
			Type actualType = ((ClassInstanceCreation) init).getType();
			actualInstanceType = getNameOfType(actualType);
			initNode = new SeedCreationNode(nodeId++, varName, seed, SeedCreationType.INIT);
		} else if (init instanceof MethodInvocation){
			init.accept(this);
			MethodInvokRef currMeth = getCurrentMethodInvokRef();
			if(StringUtils.isNotEmpty(currMeth.getTargetType()) && !StringUtils.equals("<init>", currMeth.getMethodName())){
				InvocationNode invocNode = (InvocationNode)createNode(node, currMeth);
				initNode = new SeedCreationNode(nodeId++, varName, SeedCreationType.METHOD, invocNode);
			}
		}
		
		if(initNode != null && (varType.startsWith(seed) || actualInstanceType.startsWith(seed))){
			addEdge(currentNode, initNode);
			currentNode = initNode;
			nodeStack.peek().varName = varName;
			nodeStack.peek().seeded = true;
		}
		
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		if (!nodeStack.isEmpty()) {
			ParseState parseState = nodeStack.pop();
			currentNode = parseState.node;
			DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>> graphBuilder = gbStack
					.pop();
			DirectedGraph<Node, DirectedEdge> graph = graphBuilder.build();
			if (graph.vertexSet() != null && graph.vertexSet().size() > (minVertices)) {
				NamedDirectedGraph g = new NamedDirectedGraph(graph, String.valueOf(graphId++), 
						node.getName().getFullyQualifiedName(), parseState.varName, 
						parseState.methodName, this.fileName, parseState.paramTypes, node.getStartPosition(), node.getStartPosition() +  node.getLength());
				methodCFGs.add(g);
			}
		}
		
		super.endVisit(node);
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean visit(MethodInvocation node) {
		super.visit(node);
		
		if(nodeStack.isEmpty()){
			return false;
		}
		
		if(!nodeStack.peek().seeded){
			//This is not seeded so no point recording this call
			return true;
		}
		
		MethodInvokRef currMethInvok = getCurrentMethodInvokRef();

		Expression exp = node.getExpression();
		List args = node.arguments();

		// first visit expressions (they are evaluated first)
		if (exp != null) {
			exp.accept(this);
		}
		// then evaluate args
		for (Object o : args) {
			if (o instanceof MethodInvocation
					|| o instanceof ClassInstanceCreation) {
				((ASTNode) o).accept(this);
			}
		}

		if(currMethInvok == null)
			return false;
		
		ParseState parseState = nodeStack.peek();

		if(currMethInvok != null && parseState != null && StringUtils.equals(currMethInvok.getTarget(), parseState.varName)){
			Node next = createNode(node, currMethInvok);
			addEdge(currentNode, next);
			currentNode = next;
		}

		//This will make it DAG
		//connectWithParents(node, currMethInvok);

		return false;
	}

	public List<NamedDirectedGraph> getMethodCFGs() {
		return methodCFGs;
	}

	public int getMinVertices() {
		return minVertices;
	}

	public void setMinVertices(int minVertices) {
		this.minVertices = minVertices;
	}

	private Node addRoot(
			MethodDeclaration node,
			DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>> graphBuilder) {
		String label = "ROOT";
		Node root = new LabelNode(nodeId++, label);
		graphBuilder.addVertex(root);
		return root;
	}

	private String addParamNode(MethodDeclaration node, Node root) {
		String seedVar = "";
		Set<String> paramTypes = new HashSet<>();
		for(Object p : node.parameters()){
			if (p instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration) p;
				Type type = svd.getType();
				
				String typeName = getNonParametericType(getNameOfType(type));
				paramTypes.add(typeName);
				if(typeName.startsWith(seed)){
					seedVar = svd.getName().getIdentifier();
					Node paramNode = new SeedCreationNode(nodeId++, seedVar, seed, SeedCreationType.PARAM);
					addEdge(root, paramNode);
					currentNode = paramNode;
					break;
				}
			}
		}
		return seedVar;
	}
	
	private String getNonParametericType(String nameOfType) {
		if(nameOfType.contains("<")){
			return StringUtils.substringBefore(nameOfType, "<");
		}
		return nameOfType;
	}

	private void addEdge(Node curr, Node next) {
		if(gbStack.empty())
			return;
		DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>> graphBuilder = gbStack
				.peek();
		if (curr != null && curr != next && !curr.equals(next)) {
			try{
				graphBuilder.addEdge(curr, next);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	private Node createNode(ASTNode node, MethodInvokRef currMethInvok) {
		if (currMethInvok == null)
			return null;
		String targetType = currMethInvok.getTargetType();
		if(StringUtils.contains(targetType, ">") || StringUtils.contains(targetType, "<")){
			targetType = seed;
		}
		Node next = new InvocationNode(nodeId++, targetType,
				currMethInvok.getMethodName(), currMethInvok.getArgTypes());
		nodeMap.put(node, next);
		return next;
	}
	
	private class ParseState{
		private final Node node;
		private Boolean seeded;
		private String varName;
		private String methodName;
		private Set<String> paramTypes;
		private ParseState(Node node, Boolean seeded, String varName, String methodName, Set<String> paramTypes) {
			super();
			this.node = node;
			this.seeded = seeded;
			this.varName = varName;
			this.methodName = methodName;
			this.paramTypes = paramTypes;
		}
	}
	
	/*private void connectWithParents(MethodInvocation node,
			MethodInvokRef currMethInvok) {
		Map<String, Integer> scope = getNodeScopes().get(node);
		Map<Integer, List<ASTNode>> varBindings = getVariableBinding();

		Integer bindingId = scope.get(currMethInvok.getTarget());
		if (bindingId != null) {
			List<ASTNode> parentNodes = varBindings.get(bindingId);
			for (ASTNode p : parentNodes) {
				if (p.getStartPosition() < node.getStartPosition()) {
					ASTNode prev = extractMethodInvocationNode(p);
					Node prevNode = nodeMap.get(prev);
					if (prevNode != null && prev != node) {
						addEdge(prevNode, currentNode);
					}
				}
			}

		}
	}
*/
	/*private ASTNode extractMethodInvocationNode(ASTNode node) {

		if (node instanceof MethodDeclaration) {
			return null;
		}

		MethodInvocationVisitor methodInvokVisitor = new MethodInvocationVisitor();
		node.accept(methodInvokVisitor);
		if (methodInvokVisitor.miNode == null) {
			return extractMethodInvocationNode(node.getParent());
		}
		return methodInvokVisitor.miNode;
	}*/

	/*private class MethodInvocationVisitor extends ASTVisitor {

		protected ASTNode miNode;

		@Override
		public boolean visit(MethodInvocation node) {
			miNode = node;
			return false;
		}

		@Override
		public boolean visit(ClassInstanceCreation node) {
			miNode = node;
			return false;
		}
	};*/
	
	/*@SuppressWarnings("rawtypes")
	public boolean visit(ClassInstanceCreation node) {
		super.visit(node);
		
		if(nodeStack.isEmpty()){
			return false;
		}
		
		if(!nodeStack.isEmpty() && nodeStack.peek().seeded){
			//This is already seeded, dont want another seed
			return true;
		}
		
		MethodInvokRef currMethInvok = getCurrentMethodInvokRef();

		Expression exp = node.getExpression();
		List args = node.arguments();

		// first visit expressions (they are evaluated first)
		if (exp != null) {
			exp.accept(this);
		}
		// then evaluate args
		for (Object o : args) {
			if (o instanceof MethodInvocation
					|| o instanceof ClassInstanceCreation) {
				((ASTNode) o).accept(this);
			}
		}
		
		if(currMethInvok != null && StringUtils.startsWith(currMethInvok.getTargetType(), seed)){
			Node next = createNode(node, currMethInvok);
			addEdge(currentNode, next);
			currentNode = next;
		}
		return false;
	}*/



}
