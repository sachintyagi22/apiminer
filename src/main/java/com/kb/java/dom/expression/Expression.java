package com.kb.java.dom.expression;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class Expression implements Serializable {

	private static final long serialVersionUID = 1L;
	public abstract Collection<Expression> getSubExpressions();
	public abstract void substitute(Expression oldExp, Expression newExp);

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		return toString().equals(o.toString());
	}

	public static List<Expression> getAllSubExpressions(Expression e) {
		List<Expression> vars = new LinkedList<Expression>();

		if (e == null)
			return vars;

		vars.add(e);

		for (Expression ee : e.getSubExpressions()) {
			vars.add(ee);
			vars.addAll(getAllSubExpressions(ee));
		}

		return vars;
	}

	@Override
	public abstract Expression clone();

}
