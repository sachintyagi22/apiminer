package com.kb.java.graph;

public class ControlStructureNode extends LabelNode{

	private boolean start = true;
	private ControlStructType type;
	
	public ControlStructureNode(int id, boolean start, ControlStructType type) {
		super(id, "");
		this.start = start;
		this.type = type;
	}

	@Override
	public String getLabel() {
		StringBuffer sb = new StringBuffer();
		sb.append(start?"START " : "END ").append(type.name);
		return sb.toString();
	}
	
	public enum ControlStructType {
		FOR("FOR"), IF("IF"), WHILE("WHILE");
		private final String name;
		private ControlStructType(String name) {
	        this.name = name;
	    }
	}
}

