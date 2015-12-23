package com.kb.java.dom.expression;

import java.util.Collection;
import java.util.LinkedList;

import com.kb.java.dom.condition.ExpressionCondition;

public class Variable extends ExpressionCondition {
	
	private static final long serialVersionUID = 1L;
	protected String name;

	public Variable() {
	}

	public Variable(String string) {
		name = string;
	}

	public String getName() {
		return name;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return new LinkedList<Expression>();
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			Variable v = (Variable) o;

			return getName().equals(v.getName());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {

	}

	@Override
	public Expression clone() {
		return new Variable(name);
	}
}
