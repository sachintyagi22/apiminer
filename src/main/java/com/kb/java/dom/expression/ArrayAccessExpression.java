package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.utils.Tools;

public class ArrayAccessExpression extends Variable {
	private static final long serialVersionUID = 1L;
	private Expression array, index;

	public ArrayAccessExpression(Expression array, Expression index) {
		this.array = array;
		this.index = index;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return Tools.makeCollection(array, index);
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (array.equals(oldExp)) {
			array = newExp;
		} else {
			array.substitute(oldExp, newExp);
		}

		if (index.equals(oldExp)) {
			index = newExp;
		} else {
			index.substitute(oldExp, newExp);
		}
	}

	@Override
	public Expression clone() {
		return new ArrayAccessExpression(array.clone(), index.clone());
	}

	@Override
	public String toString() {
		return array + "[" + index + "]";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String getName() {
		return array + "[" + index + "]";
	}

}
