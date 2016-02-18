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
	
	
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (start ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ControlStructureNode other = (ControlStructureNode) obj;
		if (start != other.start)
			return false;
		if (type != other.type)
			return false;
		return true;
	}*/

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object controlStructureNode) {
		return super.equals(controlStructureNode);
	}

	public enum ControlStructType {
		FOR("FOR"), IF("IF"), WHILE("WHILE");
		private final String name;
		private ControlStructType(String name) {
	        this.name = name;
	    }
	}
}

