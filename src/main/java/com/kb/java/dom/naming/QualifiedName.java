package com.kb.java.dom.naming;

import java.util.Collection;
import java.util.LinkedList;

import com.kb.java.dom.expression.Expression;

public class QualifiedName extends Expression {
	private static final long serialVersionUID = 1L;
	String name;

	public QualifiedName(String name) {
		this.name = name;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return new LinkedList<Expression>();
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {

	}

	@Override
	public Expression clone() {
		return this;
	}

	@Override
	public String toString() {
		return name;
	}

}
