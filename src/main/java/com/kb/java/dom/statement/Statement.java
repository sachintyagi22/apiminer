package com.kb.java.dom.statement;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import com.kb.java.dom.expression.Expression;

public abstract class Statement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	int charIndex = -1; // useful for ordering later

	/**
	 * get the expressions that directly make up this statements
	 */
	public abstract Collection<Expression> getSubExpressions();

	/**
	 * Gather up all sub expressions down to the leaves
	 */
	public Collection<Expression> getAllSubExpressions() {

		Set<Expression> allExpressions = Sets.newHashSet();

		for (Expression e : getSubExpressions()) {
			allExpressions.addAll(Expression.getAllSubExpressions(e));
		}

		return allExpressions;

	}

	public abstract void substitute(Expression oldExp, Expression newExp);

	@Override
	public abstract Statement clone();

	@Override
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public void setCharIndex(int charIndex) {
		this.charIndex = charIndex;
	}

	public int getCharIndex() {
		return charIndex;
	}
}
