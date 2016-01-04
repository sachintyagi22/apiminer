package com.kb.java.graph;



public class BreakNode extends CFNode {

	private CFNode next;

	public BreakNode(CFNode next) {
		this.next = next;
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
		return "BREAK";
	}
	
	@Override
	public String toString() {
		return "break to " + next;
	}
}
