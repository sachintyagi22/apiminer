package com.kb.ml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Ranking<T> implements Iterable<T> {
	// sorter needed

	List<T> objects;
	HashMap<T, Integer> rankMap = new HashMap<T, Integer>();

	public Ranking(List<T> objects) {
		this.objects = objects;
		int rank = 0;

		for (T t : objects) {
			rankMap.put(t, rank++);
		}
	}

	public int getRank(T t) {
		if (!contains(t))
			return -1;

		return rankMap.get(t);
	}

	public boolean contains(T t) {
		return this.objects.contains(t);
	}

	public Iterator<T> iterator() {
		return objects.iterator();
	}

	public int size() {
		return objects.size();
	}
}
