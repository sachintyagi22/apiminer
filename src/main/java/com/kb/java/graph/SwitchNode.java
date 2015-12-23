package com.kb.java.graph;

import java.util.LinkedList;
import java.util.List;

import com.kb.java.dom.condition.AndCondition;
import com.kb.java.dom.condition.Condition;
import com.kb.java.dom.expression.Expression;

public class SwitchNode extends CFNode {
	private Expression exp;
	private List<CFNode> cases = new LinkedList<>();

	public SwitchNode(Expression exp) {
		this.exp = exp;
	}

	@Override
	public CFNode getNext() {
		//TODO: FixMe
		return null;
	}

	public void addCase(SwitchToCaseNode stcn) {
		cases.add(stcn);
	}

	@Override
	public String toString() {
		return "switch( " + exp + ")";
	}

	public Expression getExpression() {
		return exp;
	}

	public Condition getDefaultCondition() {
		AndCondition ret = new AndCondition();

		for (CFNode caseNode : cases) {
			SwitchToCaseNode stcn = (SwitchToCaseNode) caseNode;

			if (!stcn.isDefault()) {
				ret.addCondition(stcn.getCondition().negated());
			}
		}

		return ret;
	}

}
