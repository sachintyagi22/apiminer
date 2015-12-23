package com.kb.java.dom.expression;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.kb.java.dom.condition.ExpressionCondition;

/**
 * Calling a method Separate class for static method call?
 * 
 */
public class InvocationExpression extends ExpressionCondition {

	private static final long serialVersionUID = 1L;

	Expression target;

	List<Expression> parameters;

	String method;

	public InvocationExpression(Expression target, String method,
			List<Expression> parameters) {
		this.target = target;
		this.method = method;
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		if (isTrue) {
			if (target != null)
				return target + "." + method + paramString();
			else
				return method + paramString();
		} else {
			if (target != null)
				return "!" + target + "." + method + paramString();
			else
				return "!" + method + paramString();
		}
	}

	private String paramString() {
		StringBuffer ret = new StringBuffer();
		boolean first = true;

		ret.append("(");

		for (Expression e : parameters) {
			if (!first) {
				ret.append(", ");
			}

			ret.append(e);

			first = false;
		}

		ret.append(")");

		return ret.toString();
	}

	public Expression getTarget() {
		return target;
	}

	public void setTarget(Expression e) {
		target = e;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		LinkedList<Expression> sub = new LinkedList<Expression>();

		sub.add(target);
		sub.addAll(parameters);

		return sub;
	}

	public String getInvokedMethod() {
		return method;
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (target != null) {
			if (target.equals(oldExp)) {
				target = newExp;
			} else {
				target.substitute(oldExp, newExp);
			}
		}

		for (int i = 0; i < parameters.size(); i++) {
			Expression e = parameters.get(i);

			if (e.equals(oldExp)) {
				parameters.set(i, newExp);
			}

			parameters.get(i).substitute(oldExp, newExp);
		}
	}

	@Override
	public Expression clone() {
		// TODO clone param list

		if (target != null)
			return new InvocationExpression(target.clone(), method, parameters);
		else
			return new InvocationExpression(null, method, parameters);
	}

}
