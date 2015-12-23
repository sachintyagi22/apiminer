package com.kb.java.graph;

import com.kb.java.dom.condition.Condition;

public class WhileNode extends CFNode {

	Condition condition;
	CFNode bodyNode;
	CFNode exitNode;

	public WhileNode(Condition condition) {
		this.condition = condition;
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

	@Override
	public String toString() {
		return "while( " + condition + " )";
	}

	public Condition getCondition() {
		return condition;
	}

}
