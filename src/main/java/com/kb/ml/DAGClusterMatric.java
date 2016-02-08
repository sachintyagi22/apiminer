package com.kb.ml;

import com.google.common.base.Predicate;
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

        Set<Node> nodeSet1 = Sets.filter(graph1.vertexSet(), filterNodeSet("FileChannel"));
        Set<Node> nodeSet2 = Sets.filter(graph2.vertexSet(), filterNodeSet("FileChannel"));

        Set<DirectedEdge> edgeSet1 = new HashSet<>();
        Set<DirectedEdge> edgeSet2 = new HashSet<>();
        for(Node node : nodeSet1){
            edgeSet1.addAll(graph1.edgesOf(node));
        }
        for(Node node : nodeSet2){
            edgeSet2.addAll(graph2.edgesOf(node));
        }

        Set<Node> filteredNodeSet1 = new HashSet<>();
        for(DirectedEdge edge : edgeSet1){
            filteredNodeSet1.add(graph1.getEdgeSource(edge));
            filteredNodeSet1.add(graph1.getEdgeTarget(edge));
        }

        Set<Node> filteredNodeSet2 = new HashSet<>();
        for(DirectedEdge edge : edgeSet2){
            filteredNodeSet2.add(graph2.getEdgeSource(edge));
            filteredNodeSet2.add(graph2.getEdgeTarget(edge));
        }

        Set<Node> unionOfNodes = getUnion(filteredNodeSet1, filteredNodeSet2);
        Sets.SetView<Node> intersectionOfNodes = Sets.intersection(filteredNodeSet1, filteredNodeSet2);
//        Set<DirectedEdge> edgeSet1 = graph1.edgeSet();
//        Set<DirectedEdge> edgeSet2 = graph2.edgeSet();
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

    private Predicate<Node> filterNodeSet(final String filterStr){
        return new Predicate<Node>() {
            @Override
            public boolean apply(Node node) {
                return node.getLabel().contains(filterStr);
            }
        };
    }
}
