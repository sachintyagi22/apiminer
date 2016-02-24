package com.kb.ml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.kb.java.model.Cluster;
import com.kb.utils.Permute;
import com.kb.utils.Tuple;

public class KMedoids<T> {
	public static int MAXITERATIONS = 500;

	DistanceMetric<T> dist;
	int k;

	Configuration<T> config;

	public KMedoids(DistanceMetric<T> dist, int k) {
		this.dist = dist;
		this.k = k;
	}

	public double getCost() {
		return config.computeCost();
	}

	public void buildClusterer(List<T> trainingSet) {

		// 1. Initialize: randomly select k of the n data points as the mediods
		config = new Configuration<T>(trainingSet, k, dist);

		// 2. Associate each data point to the closest medoid.
		int iterCount = 0;
		boolean change = true;

		while (change) {
			iterCount++;
			System.out.println("[KMedoids] iter #" + iterCount);

			if (iterCount >= MAXITERATIONS) {
				break;
			}

			change = false;

			// 3. For each mediod m
			for (T m : config.getMedoids()) {
				// 1. For each non-medoid data point o
				for (T o : config.getRest()) {
					// 1. Swap m and o and compute the total cost of the
					// configuration
					Configuration<T> cPrime = config.swap(m, o);

					// 4. Select the configuration with the lowest cost.
					if (cPrime.computeCost() < config.computeCost()) {
						config = cPrime;
						change = true;
					}
					// 5. repeat steps 2 to 5 until there is no change in the
					// medoid.
				}

			}
		}
	}

	public T getMedoid(T t) {
		if (config == null)
			throw new IllegalStateException(
					"You must build the clusterer first!");

		return config.getMedoidFor(t);
	}

	public List<Cluster<T>> getClusters(Collection<T> data) {
		List<Cluster<T>> clusters = new ArrayList<Cluster<T>>();
		int corpusSize = data.size();
		for (T medoid : config.getMedoids()) {
			List<T> ls = new ArrayList<T>();
			for (T o : data) {
				if (this.getMedoid(o).equals(medoid)) {
					ls.add(o);
				}
			}
			clusters.add(new Cluster<T>(ls, medoid, corpusSize));
		}
		return clusters;
	}

	public void printClusters(Collection<T> data) {
		List<Cluster<T>> clusters = getClusters(data);
		int i = 0;
		for (Cluster<T> c : clusters) {
			T m = this.getMedoid(c.getMean());
			// c.sort((e1, e2) -> Double.compare(dist.getDistance(m, e1),
			// dist.getDistance(m, e2)));
			System.out.println("\n\n\n\n **** Cluster " + i + " {" + m
					+ "} ****");
			for (T n : c.getInstances()) {
				System.out.println(n.toString() + "  ~  "
						+ dist.getDistance(m, n));
			}
			i++;
		}

	}

	public static Tuple<Double, Double> makePoint(double t1, double t2) {
		return new Tuple<Double, Double>(t1, t2);
	}

	public List<List<T>> cluster(List<T> data) {
		List<List<T>> clusters = new LinkedList<List<T>>();

		for (T medoid : config.getMedoids()) {
			List<T> cluster = new LinkedList<T>();

			for (T o : data) {
				if (this.getMedoid(o).equals(medoid)) {
					cluster.add(o);
				}
			}

			clusters.add(cluster);
		}

		return clusters;
	}
}

/**
 * Immutable Configuration
 */
class Configuration<T> {
	List<T> medoids;
	List<T> rest;
	DistanceMetric<T> dist;

	double cost = -1;

	public Configuration(List<T> trainingSet, int k, DistanceMetric<T> dist) {
		this.dist = dist;

		if (k > trainingSet.size()) {
			k = trainingSet.size();
		}

		medoids = Permute.randomSubList(trainingSet, k);
		rest = subtract(trainingSet, medoids);
	}

	private Configuration() {

	}

	public List<T> getRest() {
		return cloneList(rest);
	}

	public List<T> getMedoids() {
		return cloneList(medoids);
	}

	public Configuration<T> swap(T m, T o) {
		if (!medoids.contains(m) || !rest.contains(o))
			return this;

		double total = 0;

		Configuration<T> c = new Configuration<T>();
		c.dist = dist;
		c.medoids = new LinkedList<T>();
		c.rest = new LinkedList<T>();

		for (T med : medoids) {
			if (!med.equals(m) && !med.equals(o)) {
				c.medoids.add(med);
			}
		}
		c.medoids.add(o);

		for (T obj : rest) {
			if (!obj.equals(o) && !obj.equals(m)) {
				c.rest.add(obj);
				// Compute cost while constructing only
				T md = c.getMedoidFor(obj);
				total += dist.getDistance(obj, md);
			}
		}

		c.rest.add(m);
		T md = c.getMedoidFor(m);
		total += dist.getDistance(m, md);

		c.cost = total;
		return c;
	}

	public double computeCost() {
		if (cost >= 0)
			return cost;

		double total = 0;

		for (T o : rest) {
			T m = getMedoidFor(o);
			total += dist.getDistance(o, m);
		}

		cost = total;

		return cost;
	}

	public T getMedoidFor(T o) {
		if (medoids.contains(o))
			return o;

		T bestMedoid = null;
		double minDistance = Double.MAX_VALUE;

		for (T m : medoids) {
			double distance = dist.getDistance(o, m);

			if (bestMedoid == null || distance < minDistance) {
				minDistance = distance;
				bestMedoid = m;
			}
		}

		return bestMedoid;
	}

	@Override
	public String toString() {
		return "medoids: " + medoids + "\nrest: " + rest + "\ncost="
				+ this.computeCost();
	}

	private List<T> subtract(List<T> list, List<T> rem) {
		List<T> ret = new LinkedList<T>();

		for (T t : list) {
			if (!rem.contains(t)) {
				ret.add(t);
			}
		}

		return ret;
	}

	private static <T> List<T> cloneList(List<T> l) {
		List<T> ret = new LinkedList<T>();
		ret.addAll(l);
		return ret;
	}

}
