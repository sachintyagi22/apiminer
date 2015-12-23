package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.utils.Tools;

public class NegativeExpression extends Expression {
	private static final long serialVersionUID = 1L;
	Expression target;

	NegativeExpression(Expression target) {
		this.target = target;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return Tools.makeCollection(target);
	}

	@Override
	public String toString() {
		return "-" + target;
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (target.equals(oldExp)) {
			target = newExp;
		}

		target.substitute(oldExp, newExp);

	}

	@Override
	public Expression clone() {
		return new NegativeExpression(target.clone());
	}

}
