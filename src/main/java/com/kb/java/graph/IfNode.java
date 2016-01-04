package com.kb.java.graph;

import java.util.ArrayList;
import java.util.List;

import com.kb.java.dom.condition.Condition;

public class IfNode extends CFNode {
	
	private Condition condition;
	private CFNode thenNode;
	private CFNode elseNode;
	private CFNode exitNode;

	public IfNode(Condition condition) {
		this.condition = condition;
	}

	public void setThenNode(CFNode thenNode) {
		this.thenNode = thenNode;
	}

	public void setElseNode(CFNode elseNode) {
		this.elseNode = elseNode;
	}

	public void setExitNode(CFNode exitNode) {
		this.exitNode = exitNode;
	}

	public CFNode getThenNode() {
		return thenNode;
	}

	public CFNode getElseNode() {
		return elseNode;
	}

	public CFNode getExitNode() {
		return exitNode;
	}
	
	@Override
	public List<CFNode> getSubgraphs() {
		ArrayList<CFNode> subGraphs = new ArrayList<CFNode>();
		subGraphs.add(thenNode);
		subGraphs.add(elseNode);
		return subGraphs;
	}
	
	@Override
	public String getLabel() {
		return "IF: " + condition;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("if( " + condition + " )");
		b.append("{ \n " + thenNode.toString() + "\n } \n" );
		if(elseNode !=null){
			b.append("else { \n " + elseNode.toString() + "\n } \n" );
		}
		if(getNext()!=null){
			b.append(getNext().toString());
		}
		return b.toString();
	}

	@Override
	public CFNode getNext() {
		return exitNode;
	}

	public Condition getCondition() {
		return condition;
	}

}
