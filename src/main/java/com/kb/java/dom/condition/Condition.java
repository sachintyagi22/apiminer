package com.kb.java.dom.condition;

import java.util.Collection;

import com.kb.java.dom.expression.Expression;

public abstract class Condition extends Expression {

	private static final long serialVersionUID = 1L;

	public abstract Condition negated();

	public Condition or(Condition c) {
		return new OrCondition(this, c);
	}

	public Condition and(Condition c) {
		return new AndCondition(this, c);
	}

	protected <T> String stringifyList(Collection<T> things,
			String seperator) {
		StringBuffer br = new StringBuffer();

		boolean first = true;

		for (T o : things) {
			if (!first) {
				br.append(" " + seperator + " " + o);
			} else {
				br.append(o.toString());
				first = false;
			}
		}

		return br.toString();
	}
}
