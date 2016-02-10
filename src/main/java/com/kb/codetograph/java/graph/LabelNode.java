package com.kb.codetograph.java.graph;

public class LabelNode implements Node{
	private int id;
	private String label;

	public LabelNode(int id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


}
