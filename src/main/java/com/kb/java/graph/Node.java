package com.kb.java.graph;

public class Node {

	public static final int ROOT = 0;
	public static final int METHOD_INVOK = 1;
	public static final int IF_START = 2;
	public static final int IF_END = 3;
	public static final int CONDITION = 4;
	public static final int WHILE_START = 5;
	public static final int WHILE_END = 6;
	public static final int FOR_START = 7;
	public static final int FOR_END = 8;
	

	private String label;
	private int type;
	private int id;
	
	public Node(String label, int type, int id) {
		this.label = label;
		this.type = type;
		this.id = id;
	}
	
	public int getId() {
		return id;
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
		result = prime * result + id;
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
		if (id != other.id)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [label=" + label + ", type=" + type + ", id=" + id + "]";
	}

}
