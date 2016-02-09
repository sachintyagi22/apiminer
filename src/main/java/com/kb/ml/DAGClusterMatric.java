package com.kb.ml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.NamedDirectedGraph;
import com.kb.java.graph.Node;

/**
 * Created by jatina on 4/2/16.
 */
public class DAGClusterMatric implements DistanceMetric<NamedDirectedGraph> {
	
	private final Map<GraphComparisonKey, Double> cache;
	private final String filter;
	public int hits = 0;
	public int miss = 0;
	
	public int getSize() {
		return cache.size();
	}
	
	public int getHits() {
		return hits;
	}

	public int getMiss() {
		return miss;
	}

	public DAGClusterMatric(String filter) {
		this(filter, 16);
	}
	
	
	public DAGClusterMatric(String filter, Integer graphSize) {
		this.filter = filter;
		cache = new HashMap<GraphComparisonKey, Double>(graphSize);
	}
	
    @Override
    public double getDistance(NamedDirectedGraph graph1, NamedDirectedGraph graph2) {

    	GraphComparisonKey key = new GraphComparisonKey(graph1.getId(), graph2.getId());
    	Double dist = cache.get(key);
		
    	if(dist != null){
    		hits++;
    		return dist;
    	}
    	
    	miss++;
        
		dist = calculateDistance(graph1, graph2);
        cache.put(key, dist);
        return dist;
    }

	private Double calculateDistance(NamedDirectedGraph g1, NamedDirectedGraph g2) {
		Double dist;
		Set<Node> nodeSet1 = Sets.filter(g1.vertexSet(), filterNodeSet(filter));
        Set<Node> nodeSet2 = Sets.filter(g2.vertexSet(), filterNodeSet(filter));

        /*Set<DirectedEdge> edgeSet1 = new HashSet<>();
        Set<DirectedEdge> edgeSet2 = new HashSet<>();
        for(Node node : nodeSet1){
            edgeSet1.addAll(g1.edgesOf(node));
        }
        for(Node node : nodeSet2){
            edgeSet2.addAll(g2.edgesOf(node));
        }

        Set<Node> filteredNodeSet1 = new HashSet<>();
        for(DirectedEdge edge : edgeSet1){
            filteredNodeSet1.add(g1.getEdgeSource(edge));
            filteredNodeSet1.add(g1.getEdgeTarget(edge));
        }

        Set<Node> filteredNodeSet2 = new HashSet<>();
        for(DirectedEdge edge : edgeSet2){
            filteredNodeSet2.add(g2.getEdgeSource(edge));
            filteredNodeSet2.add(g2.getEdgeTarget(edge));
        }*/

        Set<Node> filteredNodeSet1 = nodeSet1;
        Set<Node> filteredNodeSet2 = nodeSet2;
        Set<Node> unionOfNodes = getUnion(filteredNodeSet1, filteredNodeSet2);
        Sets.SetView<Node> intersectionOfNodes = Sets.intersection(filteredNodeSet1, filteredNodeSet2);
        //Sets.SetView<Node> intersectionOfNodes = Sets.intersection(nodeSet1, nodeSet2);

       /* Set<DirectedEdge> unionOfEdges = getUnion(edgeSet1, edgeSet2);
        Sets.SetView<DirectedEdge> intersectionOfEdges = Sets.intersection(edgeSet1, edgeSet2);*/
        dist = getDissimilarityValue(unionOfNodes.size(), intersectionOfNodes.size()/*, unionOfEdges.size(), intersectionOfEdges.size()*/);
        //dist = getDissimilarityValue(0, intersectionOfNodes.size());
		return dist;
	}


    private Double getDissimilarityValue(int nodeUnionSize, int nodeIntersectSize) {
    	return (nodeUnionSize - nodeIntersectSize) * 1D / nodeUnionSize;
    	//return 1D / (nodeIntersectSize + 0.0001);
	}

	private double getDissimilarityValue(int nodeUnionSize, int nodeIntersectSize, int edgeUnionSize, int edgeIntersectSize) {
    	return (((nodeUnionSize - nodeIntersectSize) * 1D / nodeUnionSize) /+
                ((edgeUnionSize - edgeIntersectSize) * 1D / edgeUnionSize)) / 2D;
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
