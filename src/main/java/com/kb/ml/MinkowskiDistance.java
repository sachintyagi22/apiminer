package com.kb.ml;

import com.kb.utils.Tuple;

public class MinkowskiDistance implements DistanceMetric<Tuple<Double, Double>>
{
	double p;

	public MinkowskiDistance(double p)
	{
		this.p = p;
	}

	public double getDistance(Tuple<Double, Double> t1, Tuple<Double, Double> t2)
	{
		double inner = Math.pow(Math.abs(t1.first - t2.first), p) + Math.pow(Math.abs(t1.second - t2.second), p);

		return Math.pow(inner, (1.0 / p));
	}

}
