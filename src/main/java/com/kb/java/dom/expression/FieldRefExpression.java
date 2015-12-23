package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.utils.Tools;

public class FieldRefExpression extends Variable {
	
	private static final long serialVersionUID = 1L;
	Expression exp;

	public FieldRefExpression(Expression exp, String name) {
		this.exp = exp;
		this.name = name;
	}

	@Override
	public FieldRefExpression clone() {
		return new FieldRefExpression(exp.clone(), name);
	}

	@Override
	public String toString() {
		return exp + "." + name;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return Tools.makeCollection(exp);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FieldRefExpression)
			return o.toString().equals(toString());

		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode() * 3 + exp.hashCode();
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (exp.equals(oldExp)) {
			exp = newExp;
		} else {
			exp.substitute(oldExp, newExp);
		}
	}

}