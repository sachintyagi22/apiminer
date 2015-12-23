package com.kb.java.dom.statement;

import java.util.Collection;
import java.util.LinkedList;

import com.kb.java.dom.expression.Expression;
import com.kb.utils.Tools;

public abstract class OneExpressionStatement extends Statement {
	
	private static final long serialVersionUID = 1L;
	protected Expression exp;

	public Expression getExpression() {
		return exp;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		if (exp == null)
			return new LinkedList<Expression>();

		return Tools.makeCollection(exp);
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (exp == null)
			return;
		
		if (exp.equals(oldExp)) {
			exp = newExp;
		} else {
			exp.substitute(oldExp, newExp);
		}
	}

	@Override
	public boolean equals(Object o) {
		boolean result = false;

		if (this.getClass().equals(o.getClass()))
			if (exp.equals(((OneExpressionStatement) o).exp)) {
				result = true;
			}

		return result;
	}

	@Override
	public int hashCode() {
		return 13 * exp.hashCode() + 7;
	}
}
