package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.utils.Tools;

public class ArrayRefExpression extends Expression {
	private static final long serialVersionUID = 1L;
	private Expression target, index;

	public ArrayRefExpression(Expression target, Expression index) {
		this.target = target;
		this.index = index;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return Tools.makeCollection(target, index);
	}

	@Override
	public String toString() {
		return target.toString().replace("[]", "") + "[" + index + "]";
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (target.equals(oldExp)) {
			target = newExp;
		}

		if (index.equals(oldExp)) {
			index = newExp;
		}

		target.substitute(oldExp, newExp);
		index.substitute(oldExp, newExp);
	}

	@Override
	public Expression clone() {
		return new ArrayRefExpression(target.clone(), index.clone());
	}
}
