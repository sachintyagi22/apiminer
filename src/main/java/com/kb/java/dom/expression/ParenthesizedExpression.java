package com.kb.java.dom.expression;

public class ParenthesizedExpression extends OneOpExpressionCondition {

	private static final long serialVersionUID = 1L;

	public ParenthesizedExpression(Expression exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "(" + exp + ")";
	}

	@Override
	public Expression clone() {
		return new ParenthesizedExpression(exp.clone());
	}

}
