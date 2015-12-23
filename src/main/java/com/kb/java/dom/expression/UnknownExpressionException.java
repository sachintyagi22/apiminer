package com.kb.java.dom.expression;

public class UnknownExpressionException extends Exception {
	
	private static final long serialVersionUID = 1L;
	Object exp;

	public UnknownExpressionException(Object exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "Unknown Expression: " + exp + " Class: "
				+ exp.getClass().getName();
	}
}
