package com.kb.codetograph.ml;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class KMedoidsTest extends TestCase {

	private List<String> makeList(String... strings) {
		LinkedList<String> list = new LinkedList<String>();
		for (String s : strings) {
			list.add(s);
		}
		return list;
	}

	public void testKMedoids() {
		List<List<String>> instances = new LinkedList<List<String>>();

		instances.add(makeList("<init>", "readLine", "close"));
		// instances.add(makeList("<init>", "readLine", "close"));
		instances.add(makeList("<init>", "ready", "readLine", "close"));
		instances.add(makeList("<init>", "ready", "close"));
		// instances.add(makeList("<init>", "ready", "readLine", "close"));
		// instances.add(makeList("<init>", "ready", "close"));
		instances.add(makeList("<init>", "ready", "readLine"));
		// instances.add(makeList("<init>", "ready", "close"));
		// instances.add(makeList("<init>", "ready", "close"));
		// instances.add(makeList("<init>", "readLine", "close"));
		// instances.add(makeList("<init>", "readLine", "close"));
		// instances.add(makeList("<init>", "ready", "readLine", "close"));
		// instances.add(makeList("<init>", "ready", "close"));
		// instances.add(makeList("<init>", "ready", "readLine"));
		// instances.add(makeList("<init>", "ready", "close"));
		instances.add(makeList("<init>", "readLine"));

		KMedoids<List<String>> kmedoids = new KMedoids<List<String>>(
				new KendallsTauDistanceMetric<String>(), 2);
		kmedoids.buildClusterer(instances);
		kmedoids.printClusters(instances);
	}
}
