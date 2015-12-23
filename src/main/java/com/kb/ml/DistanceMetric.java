package com.kb.ml;

public interface DistanceMetric<T> {
	public double getDistance(T t1, T t2);
}
