package com.kb.java.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.io.Serializable;

/**
 * Created by jatina on 4/2/16.
 */
public class DirectedEdge extends DefaultWeightedEdge implements Serializable {

    private static final long serialVersionUID = 1L;
    private double weight = 1D;
    @Override
    public Node getSource() {
        return (Node) super.getSource();
    }

    @Override
    public Node getTarget() {
        return (Node) super.getTarget();
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
        Node sourceNode = (Node) this.getSource();
        Node targetNode = (Node) this.getTarget();
        if(sourceNode != null) {
            result = 31 * result + sourceNode.hashCode();
        }
        if(targetNode != null) {
            result = 31 * result + targetNode.hashCode();
        }
        return result;
    }
}
