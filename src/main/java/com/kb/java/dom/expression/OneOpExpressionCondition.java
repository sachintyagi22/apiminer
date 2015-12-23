package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.java.dom.condition.ExpressionCondition;
import com.kb.utils.Tools;

public abstract class OneOpExpressionCondition extends ExpressionCondition {
	
	private static final long serialVersionUID = 1L;
	Expression exp;

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
