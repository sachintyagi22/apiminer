package com.kb.java.dom.expression;

import java.util.Collection;

import com.kb.java.dom.condition.Condition;
import com.kb.java.dom.condition.ExpressionCondition;
import com.kb.utils.Tools;

public class ComparisonExpression extends ExpressionCondition {

	private static final long serialVersionUID = 1L;
	public static String negateSymbol(String sym) {
		if (sym.equals("<"))
			return ">=";
		if (sym.equals("<="))
			return ">";
		if (sym.equals(">"))
			return "<=";
		if (sym.equals(">="))
			return "<";
		if (sym.equals("=="))
			return "!=";
		if (sym.equals("!="))
			return "==";

		return "!" + sym;
	}

	Expression opstring1, opstring2;

	String symbol;

	public ComparisonExpression(Expression opstring1, Expression opstring2,
			String symbol) {
		this.opstring1 = opstring1;
		this.opstring2 = opstring2;

		if (isTrue) {
			this.symbol = symbol.trim();
		} else {
			this.symbol = negateSymbol(symbol.trim());
		}
	}

	@Override
	public ComparisonExpression clone() {
		return new ComparisonExpression(opstring1.clone(), opstring2.clone(),
				symbol);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public Condition negated() {
		return new ComparisonExpression(opstring1, opstring2,
				negateSymbol(symbol));
	}

	@Override
	public boolean equals(Object o) {
		// TODO: fix if switch order on == or !=
		boolean result = toString().equals(o.toString());

		// System.out.println(this + " ?= " + o + " : " + result);
		return result;
	}

	@Override
	public String toString() {
		String ret;

		if (opstring2 instanceof Variable && !(opstring1 instanceof Variable)) {
			ret = opstring2 + " " + symbol + " " + opstring1;
		} else {
			ret = opstring1 + " " + symbol + " " + opstring2;
		}

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
