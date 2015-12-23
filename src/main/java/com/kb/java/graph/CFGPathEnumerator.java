package com.kb.java.graph;

import java.util.List;

/**
 * Enumerates all paths though a cfg using depth-first search
 * 
 */
public class CFGPathEnumerator {
	List<Path> pathsFound;

	public CFGPathEnumerator() {

	}

	/*public List<Path> getPaths(CFGraph cfg) {
		pathsFound = new LinkedList<Path>();

		if (cfg.getEntry() == null)
			return pathsFound;

		LinkedList<CFNode> visited = new LinkedList<CFNode>();
		visited.add(cfg.getEntry());
		depthFirst(visited);

		return pathsFound;
	}*/

	
}
