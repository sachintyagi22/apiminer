package com.kb.java.dom.condition;

/**
 * An expression which can be treated as a boolean
 * 
 */

public abstract class ExpressionCondition extends Condition {
	private static final long serialVersionUID = 1L;
	protected boolean isTrue = true;

	@Override
	public Condition negated() {
		ExpressionCondition c = (ExpressionCondition) clone();
		c.isTrue = !c.isTrue;
		return c;
	}

	@Override
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
