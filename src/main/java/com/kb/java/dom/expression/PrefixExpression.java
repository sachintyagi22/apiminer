package com.kb.java.dom.expression;

public class PrefixExpression extends OneOpExpressionCondition {
	
	private static final long serialVersionUID = 1L;
	String op;

	PrefixExpression(Expression exp, String op) {
		this.exp = exp;
		this.op = op;
	}

	@Override
	public String toString() {
		return op + exp;
	}

	@Override
	public Expression clone() {
		return new PrefixExpression(exp.clone(), op);
	}

}
