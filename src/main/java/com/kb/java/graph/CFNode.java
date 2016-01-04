package com.kb.java.graph;

import java.util.List;


public abstract class CFNode {
	
	public abstract String getLabel();
	public abstract CFNode getNext();

	public List<CFNode> getSubgraphs() {
		return null;
	}

}