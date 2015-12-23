package com.kb.java.dom.expression;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.kb.java.dom.naming.Type;

public class ArrayCreationExpression extends Expression {
	private static final long serialVersionUID = 1L;
	private ArrayInitExpression init;
	private List<Expression> dims;
	private Type type;

	public ArrayCreationExpression(Type type, List<Expression> dims,
			ArrayInitExpression init) {
		this.init = init;
		this.dims = dims;

		String typeStr = type.toString();

		typeStr = typeStr.replaceAll("\\[\\]", "");

		this.type = new Type(typeStr);
	}

	@Override
	public String toString() {
		if (init == null)
			return "new " + type.toString();
		else
			return "type" + dimString() + " = " + init;
	}

	private String dimString() {
		StringBuffer ret = new StringBuffer();

		for (Expression e : dims) {
			ret.append("[" + e + "]");
		}

		return ret.toString();
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		LinkedList<Expression> sub = new LinkedList<Expression>();

		if (init != null) {
			sub.add(init);
		}

		sub.addAll(dims);

		return sub;
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (init != null) {
			init.substitute(oldExp, newExp);
		}

		for (int i = 0; i < dims.size(); i++) {
			Expression e = dims.get(i);

			if (e.equals(oldExp)) {
				dims.set(i, newExp);
			}

			dims.get(i).substitute(oldExp, newExp);
		}
	}

	@Override
	public Expression clone() {
		return new ArrayCreationExpression(type, dims, init);
	}

}
