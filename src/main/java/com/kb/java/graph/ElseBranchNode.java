package com.kb.java.graph;


public class ElseBranchNode extends CFNode {
	
	private CFNode next;
	private CFNode branch;

	public ElseBranchNode() {
	}
	
	@Override
	public CFNode getNext() {
		return next;
	}

	public void setNext(CFNode next) {
		this.next = next;
	}

	public CFNode getBranchHead() {
		return branch;
	}

	public void setBranchHead(CFNode loopHead) {
		branch = loopHead;
	}

	@Override
	public String toString() {
		return next.toString();
	}

}
