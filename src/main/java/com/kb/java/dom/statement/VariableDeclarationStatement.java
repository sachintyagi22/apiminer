package com.kb.java.dom.statement;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.kb.java.dom.expression.AssignmentExpression;
import com.kb.java.dom.expression.Expression;
import com.kb.java.dom.expression.Variable;
import com.kb.java.dom.naming.Type;

public class VariableDeclarationStatement extends Statement {
	
	private static final long serialVersionUID = 1L;
	private Type type;
	private Variable var;
	private AssignmentExpression init;

	public VariableDeclarationStatement(Type type, Variable var) {
		this.type = type;
		this.var = var;
	}

	public VariableDeclarationStatement(Type type, Variable var, Expression init) {
		this.type = type;
		this.var = var;

		if (init != null) {
			this.init = new AssignmentExpression(var, init);
		}
	}
	
	@Override
	public String getLabel() {
		return init.getExpression().getLabel() + init.getExpression().getClass();
	}

	@Override
	public String toString() {
		if (init != null)
			return type + " " + init.getVariable() + " = "
					+ init.getExpression();

		return type + " " + var;
	}

	public Type getType() {
		return type;
	}

	public Variable getVariable() {
		return var;
	}

	public boolean hasInit() {
		return init != null;
	}

	public AssignmentExpression getInitExpression() {
		return init;
	}

	@Override
	public Collection<Expression> getSubExpressions() {
		return Lists.<Expression> newArrayList(init);
	}

	@Override
	public void substitute(Expression oldExp, Expression newExp) {
		if (init != null) {
			if (init.equals(oldExp) && newExp instanceof AssignmentExpression) {
				init = (AssignmentExpression) newExp;
			} else {
				init.substitute(oldExp, newExp);
			}
		}
	}

	@Override
	public Statement clone() {
		VariableDeclarationStatement vds = new VariableDeclarationStatement(
				type, var);
		vds.init = (AssignmentExpression) init.clone();
		vds.charIndex = charIndex;
		return vds;

	}

}
