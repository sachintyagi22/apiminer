package com.kb.java.dom.condition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.kb.java.dom.expression.Expression;

public class AndCondition extends Condition implements Iterable<Condition> {
	private static final long serialVersionUID = 1L;
	// The things anded-together
	Set<Condition> conditions = new HashSet<Condition>();

	public AndCondition() {

	}

	public AndCondition(Condition... conditions) {
		for (Condition c : conditions) {
			this.conditions.add(c);
		}
	}

	public void addCondition(Condition c) {
		conditions.add(c);
	}

	@Override
	public Condition and(Condition c) {
		AndCondition ret = (AndCondition) clone();
		ret.addCondition(c);
		return ret;
	}

	public Collection<Condition> getConditions() {
		return conditions;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		Collection<Expression> expressions = new LinkedList<Expression>();
		expressions.addAll(conditions);
		return expressions;
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (conditions.contains(oldExp)) {
			conditions.remove(oldExp);
			conditions.add((Condition) newExp);
		}

		for (Condition c : this) {
			c.substitute(oldExp, newExp);
		}
	}

	@Override
	public Expression clone() {
		AndCondition ret = new AndCondition();

		for (Condition c : this) {
			ret.addCondition(c);
		}

		return ret;
	}

	/**
	 * Returns an Or condition with all the conditions negated
	 */
	@Override
	public Condition negated() {
		OrCondition ret = new OrCondition();

		for (Condition c : this) {
			ret.addCondition(c.negated());
		}

		return ret;
	}

	@Override
	public Iterator<Condition> iterator() {
		return conditions.iterator();
	}

	@Override
	public String toString() {
		return stringifyList(conditions, "&&");
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AndCondition)
			return conditions.equals(((AndCondition) o).conditions);
		return false;
	}

	@Override
	public int hashCode() {
		return conditions.hashCode() + 2;
	}
}
