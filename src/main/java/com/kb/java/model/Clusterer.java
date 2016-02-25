package com.kb.java.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.kb.java.graph.NamedDirectedGraph;
import com.kb.ml.DAGClusterMatric;
import com.kb.ml.KMedoids;

public class Clusterer implements Serializable{

	private final static Logger LOG = Logger.getLogger(Clusterer.class);

	public List<Cluster<NamedDirectedGraph>> getClusters(Collection<NamedDirectedGraph> instances, int n) {
		DAGClusterMatric dagClusterMatric = new DAGClusterMatric(instances.size());
		KMedoids<NamedDirectedGraph> kMedoids = new KMedoids<>(dagClusterMatric, n);
		long start = System.currentTimeMillis();
		kMedoids.buildClusterer(new ArrayList<NamedDirectedGraph>(instances));
		long end = System.currentTimeMillis();
		int total = dagClusterMatric.getHits() + dagClusterMatric.getMiss();
		
		if(LOG.isDebugEnabled()){
			LOG.debug("Time taken for clustering : " + instances.size()
					+ " graphs was " + (end - start)
					+ "mili secs, Cache hit ratio : " + dagClusterMatric.getHits()
					* 100D / total + ", Cache size: " + dagClusterMatric.getMiss());
		}

		List<Cluster<NamedDirectedGraph>> clusters = kMedoids.getClusters(instances);
		return clusters;
	}
	
	public List<Cluster<NamedDirectedGraph>> getClusters(Collection<NamedDirectedGraph> instances, int n, double support) {
		final List<Cluster<NamedDirectedGraph>> clusters = getClusters(instances, n);
		List<Cluster<NamedDirectedGraph>> filtered = new ArrayList<Cluster<NamedDirectedGraph>>();
		for(Cluster<NamedDirectedGraph> c : clusters){
			if(c.getSizeFraction() >= support){
				filtered.add(c);
			}
		}
		return filtered;
	}
}
