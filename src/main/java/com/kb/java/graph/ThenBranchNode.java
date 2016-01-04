package com.kb.java.graph;


public class ThenBranchNode extends CFNode {
	private CFNode next;
	private CFNode branch;

	public ThenBranchNode() {

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
	public String getLabel() {
		return "THEN:" + branch.getLabel();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(next.toString());
		return sb.toString();
		
	}

}
