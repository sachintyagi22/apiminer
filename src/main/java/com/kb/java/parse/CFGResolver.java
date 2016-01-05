package com.kb.java.parse;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.DirectedGraphBuilder;

import com.kb.java.graph.Node;

public class CFGResolver extends MethodInvocationResolver {

	private int id = 0;
	private Node root = new Node("ROOT", 0, id++);
	private Node current = root;
	private DirectedGraph<Node, DefaultEdge> baseGraph = new DefaultDirectedGraph<Node, DefaultEdge>(
			DefaultEdge.class);
	private DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>> graphBuilder = new DirectedGraphBuilder<Node, DefaultEdge, DirectedGraph<Node, DefaultEdge>>(
			baseGraph);

	public CFGResolver() {
		// Add the root node.
		graphBuilder.addVertex(root);
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		// For each method create a new branch from root
		current = root;
		return super.visit(node);
	}
	
	public boolean visit(ClassInstanceCreation node) {
		super.visit(node);
		MethodInvokRef currMethInvok = getCurrentMethodInvokRef();
		Node next = new Node(getMethodLabel(currMethInvok), Node.METHOD_INVOK, id++);
		graphBuilder.addEdge(current, next);
		current = next;
		return true;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		super.visit(node);
		MethodInvokRef currMethInvok = getCurrentMethodInvokRef();
		Map<String, Integer> scope = getNodeScopes().get(node);
		Map<Integer, List<ASTNode>> varBindings = getVariableBinding();
		Integer bindingId = scope.get(currMethInvok.getTarget());
		if(bindingId != null){
			List<ASTNode> parentNodes = varBindings.get(bindingId);
			for(ASTNode p : parentNodes){
				if(p.getStartPosition() < node.getStartPosition()){
					System.out.println("For node " + node + " parent : " + p);
				}
			}
			
		}
		Node next = new Node(getMethodLabel(currMethInvok), Node.METHOD_INVOK, id++);
		graphBuilder.addEdge(current, next);
		current = next;
		return true;
	}

	private String getMethodLabel(MethodInvokRef m) {
		StringBuffer sb = new StringBuffer(m.getTargetType());
		sb.append(".").append(m.getMethodName()).append("#").append(m.getArgNum());
		return sb.toString();
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		super.endVisit(node);
	}
	
	@Override
	public boolean visit(IfStatement node) {
		Node ifStart = new Node("IF", Node.IF_START, id++);
		Node ifEnd = new Node("IF-END", Node.IF_END, id++);
		graphBuilder.addEdge(current, ifStart);
		current = ifStart;
		
		node.getExpression().accept(this);
		
		Statement thenStatement = node.getThenStatement();
		
		if(thenStatement != null){
			current = ifStart;
			thenStatement.accept(this);
			graphBuilder.addEdge(current, ifEnd);
		}
		
		Statement elseStatement = node.getElseStatement();
		if(elseStatement != null){
			current = ifStart;
			elseStatement.accept(this);
			graphBuilder.addEdge(current, ifEnd);
		}
		
		current = ifEnd;
		//Don't go inside again. We already done that above.
		return false;
	}
	
	@Override
	public void endVisit(IfStatement node) {
		super.endVisit(node);
	}

	@Override
	public boolean visit(EnhancedForStatement node) {
		return super.visit(node);
	}

	public boolean endvisit(EnhancedForStatement node) {
		return super.visit(node);
	}

	@Override
	public boolean visit(ForStatement node) {
		return super.visit(node);
	}
	
	public boolean endvisit(ForStatement node) {
		return super.visit(node);
	}

	public DirectedGraph<Node, DefaultEdge> getBaseGraph() {
		return baseGraph;
	}
	
}
