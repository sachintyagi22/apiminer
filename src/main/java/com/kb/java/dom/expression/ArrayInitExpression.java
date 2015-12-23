package com.kb.java.dom.expression;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ArrayInitExpression extends Expression {

	private static final long serialVersionUID = 1L;
	private List<Expression> parameters;

	public ArrayInitExpression(List<Expression> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return paramString();
	}

	private String paramString() {
		StringBuffer ret = new StringBuffer();
		boolean first = true;

		ret.append("{");

		for (Expression e : parameters) {
			if (!first) {
				ret.append(", ");
			}

			ret.append(e);

			first = false;
		}

		ret.append("}");

		return ret.toString();
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		LinkedList<Expression> sub = new LinkedList<Expression>();

		sub.addAll(parameters);

		return sub;
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		for (int i = 0; i < parameters.size(); i++) {
			Expression e = parameters.get(i);

			if (e.equals(oldExp)) {
				parameters.set(i, newExp);
			}

			parameters.get(i).substitute(oldExp, newExp);
		}
	}

	@Override
	public Expression clone() {

		return new ArrayInitExpression(parameters);
	}

}
