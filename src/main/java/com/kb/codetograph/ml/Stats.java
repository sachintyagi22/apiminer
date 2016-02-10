package com.kb.codetograph.ml;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Stats
{
	/**
	 * @param population
	 *            an array, the population
	 * @return the variance
	 */
	public static double variance(Collection<Double> population)
	{
		long n = 0;
		double mean = 0;
		double s = 0.0;

		for (double x : population)
		{
			n++;
			double delta = x - mean;
			mean += delta / n;
			s += delta * (x - mean);
		}
		// if you want to calculate std deviation
		// of a sample change this to (s/(n-1))
		return (s / (n - 1));
	}

	/**
	 * @param population
	 *            an array, the population
	 * @return the standard deviation
	 */
	public static double stddev(Collection<Double> population)
	{
		return Math.sqrt(variance(population));
	}

	public static double mean(Collection<Double> population)
	{
		double total = 0;

		for (double x : population)
		{
			total += x;
		}

		return total / population.size();
	}

	public static double max(Collection<Double> population)
	{
		double max = 0;

		for (double x : population)
		{
			if (x > max)
			{
				max = x;
			}
		}

		return max;
	}

	public static double min(Collection<Double> population)
	{
		double min = Double.MAX_VALUE;

		for (double x : population)
		{
			if (x < min)
			{
				min = x;
			}
		}

		return min;
	}

	public static void printStats(String label, Collection<Double> population)
	{
		System.out.println(label + "\tmax= " + max(population) + "\tmean= " + mean(population) + "\tstddev= "
				+ stddev(population));
	}

	public static void main(String[] args)
	{
		double[] numbers = {5.0, 6.0, 7.0, 8.0, 33.0, 44.0};
		double[] numbers2 = {4.0, 6.0, 7.0, 8.0, 33.0, 4.0};

		LinkedList<Double> nums = new LinkedList<Double>();

		for (double n : numbers)
		{
			nums.add(n);
		}

		LinkedList<Double> nums2 = new LinkedList<Double>();

		for (double n : numbers2)
		{
			nums2.add(n);
		}

		printStats("test", nums);

		System.out.println("median: " + median(nums));

		System.out.println("normalized: " + normalize(nums));

	}

	public static List<Double> normalize(List<Double> values)
	{
		double max = max(values);
		double min = min(values);
		max = max - min;

		List<Double> ret = new LinkedList<Double>();

		for (double d : values)
		{
			ret.add((d - min) / max);
		}

		return ret;
	}

	public static double[] makeArray(Collection<Double> values)
	{
		double[] ret = new double[values.size()];
		int i = 0;

		for (double d : values)
		{
			ret[i] = d;
			i++;
		}

		return ret;
	}

	
	public static double percentile(List<Double> data, double percentile)
	{
		Double result;

		if (data.isEmpty())
			return 0;

		Collections.sort(data);

		int index = (int) Math.floor((data.size()) * percentile);

		result = data.get(index);

		return result;

	}

	public static double median(List<Double> data)
	{

		return percentile(data, 0.5);

	}

}
