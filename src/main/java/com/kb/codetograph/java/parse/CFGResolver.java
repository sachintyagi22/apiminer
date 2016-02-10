package com.kb.codetograph.java.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.kb.codetograph.java.graph.DirectedEdge;
import com.kb.codetograph.java.graph.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.builder.DirectedGraphBuilder;

import com.kb.codetograph.java.graph.ControlStructureNode;
import com.kb.codetograph.java.graph.ControlStructureNode.ControlStructType;
import com.kb.codetograph.java.graph.InvocationNode;
import com.kb.codetograph.java.graph.LabelNode;

public class CFGResolver extends MethodInvocationResolver {

	private int nodeId = 0;
	private Node currentNode;
	private Stack<Node> nodeStack = new Stack<Node>();
	private Stack<DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>>> gbStack = new Stack<DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>>>();
	private List<DirectedGraph<Node, DirectedEdge>> methodCFGs = new ArrayList<DirectedGraph<Node, DirectedEdge>>();
	private Map<ASTNode, Node> nodeMap = new HashMap<ASTNode, Node>();
	private int minVertices = 2;

	public CFGResolver() {
	}

	public CFGResolver(Integer minVertices) {
		this.minVertices = minVertices;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		DirectedGraph<Node, DirectedEdge> currentGraph = new DefaultDirectedGraph<Node, DirectedEdge>(
				DirectedEdge.class);
		DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>> graphBuilder = new DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>>(
				currentGraph);
		gbStack.push(graphBuilder);
		String methodName = node.getName().toString();
		String label = "ROOT:" + methodName + "()#" + node.parameters().size();
		Node root = new LabelNode(nodeId++, label);
		graphBuilder.addVertex(root);
		// For each method create a new branch from root
		// current = root;
		if (currentNode != root) {
			// In case there's method decl inside method decl
			nodeStack.push(currentNode);
		}
		currentNode = root;
		return super.visit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		if (!nodeStack.isEmpty()) {
			currentNode = nodeStack.pop();
		}
		DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>> graphBuilder = gbStack
				.pop();
		DirectedGraph<Node, DirectedEdge> graph = graphBuilder.build();
		if (graph.vertexSet() != null && graph.vertexSet().size() > minVertices) {
			methodCFGs.add(graph);
		}
		super.endVisit(node);
	}

	@SuppressWarnings("rawtypes")
	public boolean visit(ClassInstanceCreation node) {
		super.visit(node);
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

		Node next = createNode(node, currMethInvok);
		addEdge(currentNode, next);
		currentNode = next;
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean visit(MethodInvocation node) {
		super.visit(node);
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

		Node next = createNode(node, currMethInvok);
		addEdge(currentNode, next);
		currentNode = next;

		Map<String, Integer> scope = getNodeScopes().get(node);
		Map<Integer, List<ASTNode>> varBindings = getVariableBinding();

		if(currMethInvok == null)
			return false;

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

		return false;
	}

	@Override
	public boolean visit(IfStatement node) {
		Node ifStart = new ControlStructureNode(nodeId++, true,
				ControlStructType.IF);
		Node ifEnd = new ControlStructureNode(nodeId++, false,
				ControlStructType.IF);
		Node prevNode = currentNode;
		addEdge(currentNode, ifStart);
		currentNode = ifStart;

		node.getExpression().accept(this);

		Statement thenStatement = node.getThenStatement();
		Boolean thenBodyPresent = false;
		if (thenStatement != null) {
			currentNode = ifStart;
			thenStatement.accept(this);
			if(currentNode != ifStart){
				//If nothing added in then statement
				addEdge(currentNode, ifEnd);
				thenBodyPresent = true;
			}
		}

		Statement elseStatement = node.getElseStatement();
		Boolean elseBodyPresent = false;
		if (elseStatement != null) {
			currentNode = ifStart;
			elseStatement.accept(this);
			if(currentNode != ifStart){
				//If nothing added in else statement
				addEdge(currentNode, ifEnd);
				elseBodyPresent = true;
			}
		}

		if(thenBodyPresent || elseBodyPresent){
			//If we had then or else body 
			currentNode = ifEnd;
		} else {
			// else remove the if start completely
			if(!gbStack.empty()){
				gbStack.peek().removeVertex(ifStart);
			}
			currentNode = prevNode;
		}
		// Don't go inside again. We already done that above.
		return false;
	}

	@Override
	public void endVisit(IfStatement node) {
		super.endVisit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		super.visit(node);
		visitLoop(node.getExpression(), node.getBody(), ControlStructType.FOR);
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		super.visit(node);
		visitLoop(node.getExpression(), node.getBody(), ControlStructType.WHILE);
		return false;

	}

	@Override
	public boolean visit(ForStatement node) {
		super.visit(node);
		visitLoop(node.getExpression(), node.getBody(), ControlStructType.FOR);
		return false;
	}

	public List<DirectedGraph<Node, DirectedEdge>> getMethodCFGs() {
		return methodCFGs;
	}

	public int getMinVertices() {
		return minVertices;
	}

	public void setMinVertices(int minVertices) {
		this.minVertices = minVertices;
	}

	private void visitLoop(Expression expression, Statement loopBody, ControlStructType controlStructType) {
		Node startNode = new ControlStructureNode(nodeId++, true,
				controlStructType);
		Node endNode = new ControlStructureNode(nodeId++, false,
				controlStructType);
		addEdge(currentNode, startNode);
		currentNode = startNode;

		if (expression != null) {
			expression.accept(this);
		}

		if (loopBody != null) {
			currentNode = startNode;
			loopBody.accept(this);
			addEdge(currentNode, endNode);
		}
		currentNode = endNode;
	}

	private void addEdge(Node curr, Node next) {
		if(gbStack.empty())
			return;
		DirectedGraphBuilder<Node, DirectedEdge, DirectedGraph<Node, DirectedEdge>> graphBuilder = gbStack
				.peek();
		if (curr != next) {
			try{
				graphBuilder.addEdge(curr, next);
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("Kaiku?");
			}
		}
	}

	private Node createNode(ASTNode node, MethodInvokRef currMethInvok) {
		if (currMethInvok == null)
			return null;
		Node next = new InvocationNode(nodeId++, currMethInvok.getTargetType(),
				currMethInvok.getMethodName(), currMethInvok.getArgTypes());
		nodeMap.put(node, next);
		return next;
	}

	private ASTNode extractMethodInvocationNode(ASTNode node) {

		if (node instanceof MethodDeclaration) {
			return null;
		}

		MethodInvocationVisitor methodInvokVisitor = new MethodInvocationVisitor();
		node.accept(methodInvokVisitor);
		if (methodInvokVisitor.miNode == null) {
			return extractMethodInvocationNode(node.getParent());
		}
		return methodInvokVisitor.miNode;
	}

	private class MethodInvocationVisitor extends ASTVisitor {

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
	};

}
