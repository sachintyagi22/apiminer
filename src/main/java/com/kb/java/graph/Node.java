package com.kb.java.graph;

public class Node {

	public static final int METHOD_INVOK = 0;
	public static final int IF_START = 1;
	public static final int IF_END = 2;
	public static final int CONDITION = 3;
	public static final int WHILE_START = 4;
	public static final int WHILE_END = 5;
	public static final int FOR_START = 6;
	public static final int FOR_END = 7;

	private String label;
	private int type;
	
	public Node(String label, int type) {
		this.label = label;
		this.type = type;
	}
	
	public String getLabel() {
		return label;
	}

	public int getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + type;
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
		Node other = (Node) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
