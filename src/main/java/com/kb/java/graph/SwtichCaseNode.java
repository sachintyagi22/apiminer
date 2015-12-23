package com.kb.java.graph;

import com.kb.java.dom.expression.Expression;

public class SwtichCaseNode extends CFNode {
	Expression exp;
	CFNode next;

	public SwtichCaseNode(Expression exp) {
		this.exp = exp;
	}

	@Override
	public CFNode getNext() {
		return next;
	}

	public void setNext(CFNode next) {
		this.next = next;
	}

	@Override
	public String toString() {
		if (exp != null)
			return "case: " + exp;
		return "default case";
	}

	public boolean isDeafult() {
		return exp == null;
	}

	public Expression getExpression() {
		return exp;
	}

}
