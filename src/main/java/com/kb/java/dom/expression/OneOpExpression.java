package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.utils.Tools;

public abstract class OneOpExpression extends Expression {
	
	private static final long serialVersionUID = 1L;
	protected Expression exp;

	public Expression getExpression() {
		return exp;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return Tools.makeCollection(exp);
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
