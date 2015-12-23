package com.kb.java.dom.expression;

import java.util.Collection;
import java.util.LinkedList;

public class SuperExpression extends Expression {

	private static final long serialVersionUID = 1L;

	@Override
	public Collection<Expression> getSubExpressions() {
		return new LinkedList<Expression>();
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {

	}

	@Override
	public Expression clone() {
		return new SuperExpression();
	}

	@Override
	public String toString() {
		return "super";
	}

}
