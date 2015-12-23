package com.kb.java.dom.expression;

import com.kb.java.dom.naming.Type;

public class InstanceOfExpression extends OneOpExpressionCondition {

	private static final long serialVersionUID = 1L;
	Type type;

	public InstanceOfExpression(Expression exp, Type type) {
		this.exp = exp;
		this.type = type;
	}

	@Override
	public InstanceOfExpression clone() {
		return new InstanceOfExpression(exp.clone(), type);
	}

	@Override
	public String toString() {
		String ret;

		if (!isTrue) {
			ret = exp + " not instanceof " + type;
		} else {
			ret = exp + " instanceof " + type;
		}

		return ret;
	}

}
