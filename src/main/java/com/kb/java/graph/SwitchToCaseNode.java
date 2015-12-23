package com.kb.java.graph;

import com.kb.java.dom.condition.Condition;
import com.kb.java.dom.expression.ComparisonExpression;

public class SwitchToCaseNode extends CFNode {
	SwitchNode switchNode;
	SwtichCaseNode caseNode;

	public SwitchToCaseNode(SwitchNode switchNode, SwtichCaseNode caseNode) {
		this.switchNode = switchNode;
		this.caseNode = caseNode;
	}

	@Override
	public CFNode getNext() {
		return null;
	}

	public Condition getCondition() {
		if (this.isDefault())
			return switchNode.getDefaultCondition();
		return new ComparisonExpression(switchNode.getExpression(),
				caseNode.getExpression(), "==");
	}

	@Override
	public String toString() {
		if (caseNode.isDeafult())
			return "Jump to case: " + switchNode.getDefaultCondition();

		return "Jump to case: " + getCondition();
	}

	public boolean isDefault() {
		return caseNode.isDeafult();
	}

}
