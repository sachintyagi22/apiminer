package com.kb.java.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.kb.java.graph.CFNode;
import com.kb.ml.KMedoids;
import com.kb.ml.KendallsTauDistanceMetric;

public class ConcreteUseClusterer {

	public static List<List<ConcreteUse>> cluster(List<ConcreteUse> concreteUses) {
		
		KMedoids<List<CFNode>> kmedoids = new KMedoids<List<CFNode>>(new KendallsTauDistanceMetric<CFNode>(), 2);
		
		List<List<CFNode>> instances = new ArrayList<List<CFNode>>();
		for (ConcreteUse c : concreteUses) {
			List<CFNode> l1 = c.getCFG().asList();
			instances.add(l1);
		}

		kmedoids.buildClusterer(instances);
		kmedoids.printClusters(instances);
		// TODO Auto-generated method stub
		return new LinkedList<>();
	}

}
