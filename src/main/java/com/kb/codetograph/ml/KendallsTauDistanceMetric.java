package com.kb.codetograph.ml;

import java.util.LinkedList;
import java.util.List;

public class KendallsTauDistanceMetric<T> implements DistanceMetric<List<T>> {

	private static <T> List<T> cloneList(List<T> l) {
		List<T> ret = new LinkedList<T>();
		ret.addAll(l);
		return ret;
	}

	public double getDistance(List<T> t1, List<T> t2) {
		List<T> a = cloneList(t1);
		List<T> b = cloneList(t2);

		a.retainAll(b);
		b.retainAll(a);

		int remFrom1 = t1.size() - a.size();
		int remFrom2 = t2.size() - b.size();

		double dist = KendallsTau.computeDistance(t1, t2) + 2 * remFrom1 + 2
				* remFrom2;

		//System.out.println(t1 + " -> " + t2 + " dist = " + dist);

		return dist;
	}

}
