package com.kb.codetograph.java.graph;

import org.jgraph.graph.DefaultEdge;

/**
 * Created by jatina on 4/2/16.
 */
public class DirectedEdge extends DefaultEdge {
	
	private static final long serialVersionUID = 1L;

	/*@Override
    public boolean equals(Object pDirectedEdge) {
        if (pDirectedEdge instanceof DirectedEdge) {
            DirectedEdge directedEdge = (DirectedEdge) pDirectedEdge;
            return this.getNextNode() == directedEdge.getNextNode() &&
                    this.getPreviousNode() == directedEdge.getPreviousNode();
        }
        return false;
    }*/
}
