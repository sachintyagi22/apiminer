package com.kb.java.dom.statement;

import java.util.Collection;
import java.util.LinkedList;

import com.kb.java.dom.expression.Expression;

public class BreakStatement extends Statement {

	private static final long serialVersionUID = 1L;

	@Override
	public Collection<Expression> getSubExpressions() {
		return new LinkedList<Expression>();
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {

	}

	@Override
	public Statement clone() {
		BreakStatement newBr = new BreakStatement();
		newBr.charIndex = charIndex;
		return newBr;
	}

}
