package com.kb.java.graph;

import com.kb.java.dom.expression.Expression;
import com.kb.java.dom.naming.Declaration;

public class EnhancedForNode extends CFNode {

	Declaration dec;
	Expression exp;
	CFNode bodyNode;
	CFNode exitNode;

	public EnhancedForNode(Declaration dec, Expression exp) {
		this.dec = dec;
		this.exp = exp;
	}

	public void setBodyNode(CFNode bodyNode) {
		this.bodyNode = bodyNode;
	}

	public void setExitNode(CFNode exitNode) {
		this.exitNode = exitNode;
	}

	public CFNode getBodyNode() {
		return bodyNode;
	}

	public CFNode getExitNode() {
		return exitNode;
	}

	@Override
	public CFNode getNext() {
		return exitNode;
	}

	@Override
	public String toString() {
		return "for( " + dec + " : " + exp + " )";
	}

	public Declaration getDeclaration() {
		return dec;
	}

	public Expression getExpression() {
		return exp;
	}

}
