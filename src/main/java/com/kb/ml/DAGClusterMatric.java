package com.kb.ml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.kb.java.graph.NamedDirectedGraph;
import com.kb.java.graph.Node;

/**
 * Created by jatina on 4/2/16.
 */
public class DAGClusterMatric implements DistanceMetric<NamedDirectedGraph> {
	
	private final Map<GraphComparisonKey, Double> cache;
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

	public DAGClusterMatric(Integer graphSize) {
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
        Set<Node> filteredNodeSet1 = g1.vertexSet();
        Set<Node> filteredNodeSet2 = g2.vertexSet();
        
        Sets.SetView<Node> intersectionOfNodes = Sets.intersection(filteredNodeSet1, filteredNodeSet2);
        Ranking<Node> ranking1 = new Ranking<Node>(g1.getAsList());
		Ranking<Node> ranking2 = new Ranking<Node>(g2.getAsList());
		int rem1 = ranking1.objects.size() - intersectionOfNodes.size();
		int rem2 = ranking2.objects.size() - intersectionOfNodes.size();
		double graphDist = computeDistance(ranking1, ranking2,ranking1.objects.size()) + 2* (rem1 + rem2);
		double nameDist = g1.getMethodName().equals(g2.getMethodName())? 0 : 1;
		Set<String> g1params = g1.getParamTypes();
		Set<String> g2params = g2.getParamTypes();
		SetView<String> inter = Sets.intersection(g1params, g2params);
		SetView<String> union = Sets.union(g1params, g2params);
		
		double paramDist = (union.size() - inter.size()) * 1D / union.size();
		
		return (((2/4D) * graphDist) + ((1/4D) * nameDist)) + ((1/4D) * paramDist);
	}

    public static <E> double computeDistance(Ranking<E> r1, Ranking<E> r2,
			int numBins) {
		if (numBins == 1)
			return 0;

		int count = 0;

		for (E x : r1) {
			for (E y : r2) // for every (unique) pair
			{
				if (x == y) {
					continue;
				}

				int dif1 = r1.getRank(x) - r1.getRank(y);
				int dif2 = r2.getRank(x) - r2.getRank(y);

				if (dif1 * dif2 < 0) {
					count++;
				}
			}
		}

		double n = r1.size();
		return (count / 2.0) / (n * (n - 1.0) / 2.0);
	}
    
}
