package com.kb.java.graph;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by jatina on 4/2/16.
 */
public class DirectedEdge extends DefaultWeightedEdge {

    private static final long serialVersionUID = 1L;
    private double weight = 1D;
    @Override
    public LabelNode getSource() {
        return (LabelNode) super.getSource();
    }

    @Override
    public LabelNode getTarget() {
        return (LabelNode) super.getTarget();
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object pDirectedEdge) {
        if (pDirectedEdge != null &&
                pDirectedEdge instanceof DirectedEdge) {
            DirectedEdge directedEdge = (DirectedEdge) pDirectedEdge;
            return this.getSource().equals(directedEdge.getSource()) &&
                    this.getTarget().equals(directedEdge.getTarget());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        LabelNode sourceNode = (LabelNode) this.getSource();
        LabelNode targetNode = (LabelNode) this.getTarget();
        if(sourceNode != null) {
            result = 31 * result + sourceNode.hashCode();
        }
        if(targetNode != null) {
            result = 31 * result + targetNode.hashCode();
        }
        return result;
    }
}
