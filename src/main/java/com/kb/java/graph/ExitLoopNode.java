package com.kb.java.graph;


public class ExitLoopNode extends CFNode {
	CFNode next;
	CFNode loopHead;

	public ExitLoopNode() {

	}

	@Override
	public CFNode getNext() {
		return next;
	}

	public void setNext(CFNode next) {
		this.next = next;
	}

	public CFNode getLoopHead() {
		return loopHead;
	}

	public void setLoopHead(CFNode loopHead) {
		this.loopHead = loopHead;
	}

	@Override
	public String toString() {
		return "Exit Loop: " + loopHead;
	}

	@Override
	public CFNode clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
