package com.kb.java.dom.statement;

import com.kb.java.dom.expression.Expression;

public class ThrowStatement extends OneExpressionStatement {

	private static final long serialVersionUID = 1L;

	public ThrowStatement(Expression expression) {
		exp = expression;
	}

	@Override
	public String toString() {
		return "throw " + exp.toString();
	}

	@Override
	public Statement clone() {
		ThrowStatement newEs = new ThrowStatement(exp.clone());
		newEs.charIndex = charIndex;
		return newEs;
	}

}
