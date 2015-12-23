package com.kb.java.dom.statement;

import com.kb.java.dom.expression.Expression;

/**
 * This is a statement consisting entirely of an Expression For example:
 * <code> System.out.println("Hello"); </code>
 * 
 *
 */
public class ExpressionStatement extends OneExpressionStatement {
	
	private static final long serialVersionUID = 1L;

	public ExpressionStatement(Expression exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return exp.toString();
	}

	@Override
	public Statement clone() {
		ExpressionStatement newEs = new ExpressionStatement(exp.clone());
		newEs.charIndex = charIndex;
		return newEs;
	}

}
