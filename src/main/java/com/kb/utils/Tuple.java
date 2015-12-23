package com.kb.utils;

import java.io.Serializable;

public class Tuple<T1, T2> implements Serializable {
	private static final long serialVersionUID = 1L;
	public T1 first;
	public T2 second;

	public Tuple() {
		// empty tuple
	}

	public Tuple(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Tuple))
			return false;

		Tuple t = (Tuple) o;

		return first.equals(t.first) && second.equals(t.second);
	}

	@Override
	public int hashCode() {
		return first.hashCode() * second.hashCode();
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + ")";
	}
}
