package com.kb.java.graph;

import com.kb.java.dom.condition.Condition;

public class ForNode extends CFNode {
	Condition condition;
	CFNode bodyNode;
	CFNode exitNode;

	public ForNode(Condition c) {
		condition = c;
	}

	public void setBodyNode(CFNode bodyNode) {
		this.bodyNode = bodyNode;
	}

	public void setExitNode(CFNode exitNode) {
		this.exitNode = exitNode;
	}

	public CFNode getBodyNode() {
		return bodyNode;
	}

	public CFNode getExitNode() {
		return exitNode;
	}

	@Override
	public CFNode getNext() {
		return exitNode;
	}

	public Condition getCondition() {
		return condition;
	}
	
	@Override
	public String getLabel() {
		return "FOR";
	}

	@Override
	public String toString() {
		return "for( [inits]; " + condition + "; [updaters] )";
	}

}
