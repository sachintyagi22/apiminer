package com.kb.java.dom.expression;

import com.kb.java.dom.naming.Type;

public class CastExpression extends OneOpExpressionCondition {
	private static final long serialVersionUID = 1L;
	private Type type;

	public CastExpression(Expression exp, Type type) {
		this.exp = exp;
		this.type = type;
	}

	@Override
	public CastExpression clone() {
		return new CastExpression(exp.clone(), type);
	}

	@Override
	public String toString() {
		String ret;

		if (!isTrue) {
			ret = "!((" + type + ") " + exp + ")";
		} else {
			ret = "(" + type + ") " + exp;
		}

		return ret;
	}

}