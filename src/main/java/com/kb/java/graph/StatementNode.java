package com.kb.java.graph;

import com.kb.java.dom.statement.Statement;

public class StatementNode extends CFNode {
	
	private Statement statement;
	private CFNode next;

	public StatementNode(Statement statement) {
		this.statement = statement;
	}

	@Override
	public CFNode getNext() {
		return next;
	}

	public void setNext(CFNode next) {
		this.next = next;
	}
	
	@Override
	public String getLabel() {
		return statement.getLabel();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(statement.toString());
		if(next != null){
			sb.append("\n" + next);
		}
		return sb.toString();
	}

	public Statement getStatement() {
		return statement;
	}

	public CFNode getSuccessor() {
		return next;
	}

}
