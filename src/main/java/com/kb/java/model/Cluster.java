package com.kb.java.model;

import java.io.Serializable;
import java.util.List;

public class Cluster<T> implements Serializable{

	private final List<T> instances;
	private final T mean;
	private final int corpusSize; 
	
	public Cluster(List<T> instances, T mean, int corpusSize) {
		super();
		this.instances = instances;
		this.mean = mean;
		this.corpusSize = corpusSize;
	}
	
	public double getSizeFraction(){
		double fraction = 0D;
		if(instances != null && corpusSize != 0){
			fraction = instances.size() * 1D/corpusSize;
		} 
		return fraction;
	}

	public List<T> getInstances() {
		return instances;
	}

	public int getSizeOfCluster() { return instances.size();}

	public T getMean() {
		return mean;
	}
	
}
