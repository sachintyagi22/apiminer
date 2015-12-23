package com.kb.java.dom.expression;


public class ElementInExpression extends OneOpExpression {

	private static final long serialVersionUID = 1L;

	public ElementInExpression(Expression exp) {
		this.exp = exp;
	}

	@Override
	public Expression clone() {
		return new ElementInExpression(exp);
	}

	@Override
	public String toString() {
		return "(element in " + exp + ")";
	}

}
