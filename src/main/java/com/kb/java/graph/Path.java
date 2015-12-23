package com.kb.java.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Path implements Iterable<CFNode> {
	List<CFNode> nodes;

	@SuppressWarnings("unchecked")
	public Path(LinkedList<CFNode> nodes) {
		this.nodes = (List<CFNode>) nodes.clone();
	}

	public Path() {
		nodes = new LinkedList<CFNode>();
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("Path: \n");

		for (CFNode node : nodes) {
			buff.append(node.toString() + "\n");
		}

		return buff.toString();
	}

	public boolean contains(CFNode node) {
		return nodes.contains(node);
	}

	public Iterator<CFNode> iterator() {
		return nodes.iterator();
	}

}
