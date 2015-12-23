package com.kb.java.dom.expression;

import java.util.Collection;
import java.util.LinkedList;

public class TypeLiteralExpression extends Expression {
	
	private static final long serialVersionUID = 1L;

	public TypeLiteralExpression(String string) {
		exp = string;
	}

	String exp;

	@Override
	public Collection<Expression> getSubExpressions() {
		return new LinkedList<Expression>();
	}

	@Override
	public String toString() {
		return exp + ".class";
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {

	}

	@Override
	public Expression clone() {
		return new TypeLiteralExpression(exp);
	}
}
