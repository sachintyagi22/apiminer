package com.kb.ml;

import com.google.common.collect.Sets;
import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.Node;
import org.jgrapht.DirectedGraph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jatina on 4/2/16.
 */
public class DAGClusterMatric implements DistanceMetric<DirectedGraph<Node, DirectedEdge>> {

    @Override
    public double getDistance(DirectedGraph<Node, DirectedEdge> graph1, DirectedGraph<Node, DirectedEdge> graph2) {
        Set<Node> nodeSet1 = graph1.vertexSet();
        Set<Node> nodeSet2 = graph2.vertexSet();
        Set<Node> unionOfNodes = getUnion(nodeSet1, nodeSet2);
        Sets.SetView<Node> intersectionOfNodes = Sets.intersection(nodeSet1, nodeSet2);
        Set<DirectedEdge> edgeSet1 = graph1.edgeSet();
        Set<DirectedEdge> edgeSet2 = graph2.edgeSet();
        Set<DirectedEdge> unionOfEdges = getUnion(edgeSet1, edgeSet2);
        Sets.SetView<DirectedEdge> intersectionOfEdges = Sets.intersection(edgeSet1, edgeSet2);
        return getDissimilarityValue(unionOfNodes.size(), intersectionOfNodes.size(), unionOfEdges.size(), intersectionOfEdges.size());
    }


    private double getDissimilarityValue(int nodeUnionSize, int nodeIntersectSize, int edgeUnionSize, int edgeIntersectSize) {
        return (((nodeUnionSize - nodeIntersectSize) / nodeUnionSize) +
                ((edgeUnionSize - edgeIntersectSize) / edgeUnionSize)) / 2;
    }


    private <T> Set<T> getUnion(Set<T> vertexSet1, Set<T> vertexSet2) {
        Set<T> unionOfNodes = new HashSet<>();
        unionOfNodes.addAll(vertexSet1);
        unionOfNodes.addAll(vertexSet2);
        return unionOfNodes;
    }
}
