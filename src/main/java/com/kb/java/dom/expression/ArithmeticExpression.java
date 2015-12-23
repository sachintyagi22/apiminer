package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.utils.Tools;

public class ArithmeticExpression extends Expression {

	private static final long serialVersionUID = 1L;
	private Expression opstring1, opstring2;
	private String symbol;

	public ArithmeticExpression(Expression opstring1, Expression opstring2,
			String symbol) {
		this.opstring1 = opstring1;
		this.opstring2 = opstring2;
		this.symbol = symbol.trim();
	}

	@Override
	public ArithmeticExpression clone() {
		return new ArithmeticExpression(opstring1.clone(), opstring2.clone(),
				symbol);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}

	@Override
	public String toString() {
		String ret = opstring1 + " " + symbol + " " + opstring2;
		return ret;
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (opstring1.equals(oldExp)) {
			opstring1 = newExp;
		}

		if (opstring2.equals(oldExp)) {
			opstring2 = newExp;
		}

		opstring1.substitute(oldExp, newExp);
		opstring2.substitute(oldExp, newExp);
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return Tools.makeCollection(opstring1, opstring2);
	}

}
