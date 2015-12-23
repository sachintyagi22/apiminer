package com.kb.java.dom.condition;

import java.util.Collection;
import java.util.LinkedList;

import com.kb.java.dom.expression.Expression;

public class True extends ExpressionCondition {

	private static final long serialVersionUID = 1L;

	@Override
	public Expression clone() {
		return new True();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof True;
	}

	@Override
	public Condition negated() {
		return new False();
	}

	@Override
	public String toString() {
		return "TRUE";
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {

	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return new LinkedList<Expression>();
	}

}
