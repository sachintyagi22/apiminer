package com.kb.codetograph.java.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kb.codetograph.java.graph.Node;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class DAGClustering {


    public List<List<DirectedGraph<Node, DefaultEdge>>> getClusters(List<DirectedGraph<Node, DefaultEdge>> graphs) {

        for (int i = 0; i < graphs.size() - 1; i++) {
            for (int j = i + 1; j < graphs.size(); j++) {
                DirectedGraph<Node, DefaultEdge> graph1 = graphs.get(i);
                DirectedGraph<Node, DefaultEdge> graph2 = graphs.get(j);
                Set<Node> nodeSet1 = graph1.vertexSet();
                Set<Node> nodeSet2 = graph2.vertexSet();
                Set<Node> unionOfNodes = getUnion(nodeSet1, nodeSet2);
                nodeSet1.retainAll(nodeSet2);
                Set<DefaultEdge> edgeSet1 = graph1.edgeSet();
                Set<DefaultEdge> edgeSet2 = graph2.edgeSet();
                Set<DefaultEdge> unionOfEdges = getUnion(edgeSet1, edgeSet2);
                edgeSet1.retainAll(edgeSet2);
                getDissimilarityValue(unionOfNodes.size(), nodeSet1.size(), unionOfEdges.size(), edgeSet1.size());
            }
        }
        return null;
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
