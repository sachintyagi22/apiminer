package com.kb.java.parse;

import java.util.ArrayList;
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

import com.kb.java.graph.ControlStructureNode;
import com.kb.java.graph.ControlStructureNode.ControlStructType;
import com.kb.java.graph.InvocationNode;
import com.kb.java.graph.LabelNode;
import com.kb.java.graph.Node;

public class CFGResolver extends MethodInvocationResolver {

	private int nodeId = 0;
	private Node currentNode;
	private Stack<Node> nodeStack = new Stack<Node>();
	private Stack<DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>>> gbStack = new Stack<DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>>>();
	private List<DirectedGraph<Node, DefaultEdge>> methodCFGs = new ArrayList<DirectedGraph<Node, DefaultEdge>>();
	private Map<ASTNode, Node> nodeMap = new HashMap<ASTNode, Node>();
	private int minVertices = 2;

	public CFGResolver() {
	}

	public CFGResolver(Integer minVertices) {
		this.minVertices = minVertices;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		DirectedGraph<Node, DefaultEdge> currentGraph = new DefaultDirectedGraph<Node, DefaultEdge>(
				DefaultEdge.class);
		DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>> graphBuilder = new DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>>(
				currentGraph);
		gbStack.push(graphBuilder);
		Node root = new LabelNode(nodeId++, "ROOT");
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
		DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>> graphBuilder = gbStack
				.pop();
		DirectedGraph<Node, DefaultEdge> graph = graphBuilder.build();
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
		addEdge(currentNode, ifStart);
		currentNode = ifStart;

		node.getExpression().accept(this);

		Statement thenStatement = node.getThenStatement();

		if (thenStatement != null) {
			currentNode = ifStart;
			thenStatement.accept(this);
			addEdge(currentNode, ifEnd);
		}

		Statement elseStatement = node.getElseStatement();
		if (elseStatement != null) {
			currentNode = ifStart;
			elseStatement.accept(this);
			addEdge(currentNode, ifEnd);
		}

		currentNode = ifEnd;
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
		visitLoop(node.getExpression(), node.getBody(), "For Start", "For End");
		return false;
	}

	@Override
	public boolean visit(WhileStatement node) {
		super.visit(node);
		visitLoop(node.getExpression(), node.getBody(), "While Start",
				"while End");
		return false;

	}

	@Override
	public boolean visit(ForStatement node) {
		super.visit(node);
		visitLoop(node.getExpression(), node.getBody(), "For Start", "For End");
		return false;
	}

	public List<DirectedGraph<Node, DefaultEdge>> getMethodCFGs() {
		return methodCFGs;
	}

	public int getMinVertices() {
		return minVertices;
	}

	public void setMinVertices(int minVertices) {
		this.minVertices = minVertices;
	}

	private void visitLoop(Expression expression, Statement loopBody,
			String startLabel, String endLabel) {
		Node startNode = new ControlStructureNode(nodeId++, true,
				ControlStructType.FOR);
		Node endNode = new ControlStructureNode(nodeId++, false,
				ControlStructType.FOR);
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
		DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>> graphBuilder = gbStack
				.peek();
		if (curr != next) {
			graphBuilder.addEdge(curr, next);
		}
	}

	private Node createNode(ASTNode node, MethodInvokRef currMethInvok) {
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
