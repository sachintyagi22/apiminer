package com.kb.java.dom.statement;

import com.kb.java.dom.expression.Expression;

public class ReturnStatement extends OneExpressionStatement {

	private static final long serialVersionUID = 1L;

	public ReturnStatement(Expression expression) {
		exp = expression;
	}

	public ReturnStatement() {
	}

	@Override
	public String toString() {
		if (exp != null)
			return "return " + exp.toString();
		else
			return "return";
	}

	@Override
	public Statement clone() {
		ReturnStatement newEs;
		if (exp != null) {
			newEs = new ReturnStatement(exp.clone());
		} else {
			newEs = new ReturnStatement();
		}
		newEs.charIndex = charIndex;
		return newEs;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ReturnStatement) {
			if (exp == null)
				return ((ReturnStatement) o).exp == null;
			else
				return exp.equals(((ReturnStatement) o).exp);
		}

		return false;
	}

	@Override
	public int hashCode() {
		if (exp != null)
			return 13 * exp.hashCode() + 7;
		else
			return 234234;
	}
}
