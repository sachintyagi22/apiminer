package com.kb.java.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.DirectedGraphBuilder;

import com.kb.java.graph.Node;

public class CFGResolver extends MethodInvocationResolver {

	private int id = 0;
	private Node root = new Node("ROOT", 0, id++);
	private Stack<Node> previous = new Stack<Node>();
	private Node current = root;
	private Map<ASTNode, Node> nodeMap = new HashMap<ASTNode, Node>();
	private DirectedGraph<Node, DefaultEdge> baseGraph = new DefaultDirectedGraph<Node, DefaultEdge>(
			DefaultEdge.class);
	private DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>> graphBuilder =
			new DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>>(baseGraph);

	public CFGResolver() {
		// Add the root node.
		graphBuilder.addVertex(root);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		// For each method create a new branch from root
		// current = root;
		if (current != root) {
			// In case there's method decl inside method decl
			previous.push(current);
		}
		current = root;
		return super.visit(node);
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		if (!previous.isEmpty()) {
			current = previous.pop();
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
		addEdge(current, next);
		current = next;
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
		addEdge(current, next);
		current = next;

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
						addEdge(prevNode, current);
					}
				}
			}

		}

		return false;
	}

	private String getMethodLabel(MethodInvokRef m) {
		StringBuffer sb = new StringBuffer(m.getTargetType());
		sb.append(".").append(m.getMethodName()).append("#")
				.append(m.getArgNum());
		return sb.toString();
	}

	@Override
	public boolean visit(IfStatement node) {
		Node ifStart = new Node("IF", Node.IF_START, id++);
		Node ifEnd = new Node("IF-END", Node.IF_END, id++);
		addEdge(current, ifStart);
		current = ifStart;

		node.getExpression().accept(this);

		Statement thenStatement = node.getThenStatement();

		if (thenStatement != null) {
			current = ifStart;
			thenStatement.accept(this);
			addEdge(current, ifEnd);
		}

		Statement elseStatement = node.getElseStatement();
		if (elseStatement != null) {
			current = ifStart;
			elseStatement.accept(this);
			addEdge(current, ifEnd);
		}

		current = ifEnd;
		// Don't go inside again. We already done that above.
		return false;
	}

	private void addEdge(Node curr, Node next) {
		if (curr.equals(next)) {
			System.out.println("here");
		}
		if (curr != next) {
			graphBuilder.addEdge(curr, next);
		}
	}

	@Override
	public void endVisit(IfStatement node) {
		super.endVisit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		Node enhancedForStart = new Node("Enchanced For Start",Node.EN_FOR_START,id++);
		Node enhancedForEnd = new Node("Enchanced For End",Node.EN_FOR_END,id++);

		graphBuilder.addEdge(current, enhancedForStart);
		current = enhancedForStart;

		node.getExpression().accept(this);
		Statement enchancedForBody = node.getBody();

		if(enchancedForBody != null){
			current = enhancedForStart;
			enchancedForBody.accept(this);
			graphBuilder.addEdge(current,enhancedForEnd);
		}
		current = enhancedForEnd;
		return false;
	}

	public boolean endvisit(EnhancedForStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		Node forStart = new Node("For Start",Node.FOR_START,id++);
		Node forEnd = new Node("For End",Node.FOR_END,id++);

		graphBuilder.addEdge(current, forStart);
		current = forStart;

		node.getExpression().accept(this);
		Statement forBody = node.getBody();

		if(forBody != null){
			current = forStart;
			forBody.accept(this);
			graphBuilder.addEdge(current,forEnd);
		}
		current = forEnd;
		return false;
	}

	public boolean endvisit(ForStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(WhileStatement node) {
		Node whileStart = new Node("While Start",Node.WHILE_START,id++);
		Node whileEnd = new Node("While End",Node.WHILE_END,id++);

		graphBuilder.addEdge(current, whileStart);
		current = whileStart;

		node.getExpression().accept(this);
		Statement whileBody = node.getBody();

		if(whileBody != null){
			current = whileStart;
			whileBody.accept(this);
			graphBuilder.addEdge(current,whileEnd);
		}
		current = whileEnd;
		return false;
	}

	public boolean endvisit(WhileStatement node) {
		return super.visit(node);
	}

	public DirectedGraph<Node, DefaultEdge> getBaseGraph() {
		return baseGraph;
	}

	private Node createNode(ASTNode node, MethodInvokRef currMethInvok) {
		Node next = new Node(getMethodLabel(currMethInvok), Node.METHOD_INVOK,
				id++);
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
