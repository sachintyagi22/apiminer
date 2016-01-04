package com.kb.java.dom.expression;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.kb.java.dom.naming.Type;

public class NewExpression extends Expression {
	
	private static final long serialVersionUID = 1L;
	Type classCreated;
	Expression target;
	List<Expression> arguments;

	public NewExpression(Expression target, Type classCreated,
			List<Expression> arguments) {
		this.target = target;
		this.classCreated = classCreated;
		this.arguments = arguments;
	}

	@Override
	public String getLabel() {
		return (target == null? ("") : (target+ "." ))+ classCreated + "<init>";
	}
	
	@Override
	public String toString() {
		if (target != null)
			return "new " + target + "." + classCreated + paramString();
		else
			return "new " + classCreated + paramString();

	}

	private String paramString() {
		StringBuffer ret = new StringBuffer();
		boolean first = true;

		ret.append("(");

		for (Expression e : arguments) {
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

		if (target != null) {
			sub.add(target);
		}
		sub.addAll(arguments);

		return sub;
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (target != null && target.equals(oldExp)) {
			target = newExp;
		}

		if (target != null) {
			target.substitute(oldExp, newExp);
		}

		for (int i = 0; i < arguments.size(); i++) {
			Expression e = arguments.get(i);

			if (e.equals(oldExp)) {
				arguments.set(i, newExp);
			}

			arguments.get(i).substitute(oldExp, newExp);
		}
	}

	@Override
	public Expression clone() {
		// TODO close prameter List

		if (target != null)

			return new NewExpression(target.clone(), classCreated, arguments);

		else

			return new NewExpression(null, classCreated, arguments);
	}
}
