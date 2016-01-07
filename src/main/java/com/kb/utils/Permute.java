package com.kb.utils;

import java.util.LinkedList;
import java.util.List;

public class Permute<T> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T>[] determinitsticPartition(List<T> list, int parts) {
		List[] lists = new LinkedList[parts];

		for (int i = 0; i < parts; i++) {
			lists[i] = new LinkedList<Object>();
		}

		int count = 0;

		for (Object o : list) {
			lists[count % parts].add(o);

			count++;
		}

		return lists;
	}

	// swaps array elements i and j
	static <T> void exch(List<T> l, int i, int j) {
		T swap = l.get(i);
		l.set(i, l.get(j));
		l.set(j, swap);
	}

	public static <T> List<T>[] randomPartition(List<T> list, int parts) {
		shuffle(list);
		return determinitsticPartition(list, parts);
	}

	// take as input an array of strings and rearrange them in random order
	public static <T> void shuffle(List<T> list) {
		int N = list.size();
		for (int i = 0; i < N; i++) {
			int r = i + (int) (Math.random() * (N - i)); // between i and N-1
			exch(list, i, r);
		}
	}

	private List<List<T>> comb;
	final static int COMBLIMIT = 32;

	public List<List<T>> allPermutations(List<T> s) {
		return comb1(s, s.size());
	}

	// print all subsets that take k of the remaining elements, with given
	// prefix
	public List<List<T>> comb1(List<T> s, int k) {

		comb = new LinkedList<List<T>>();
		comb1(s, new LinkedList<T>(), k);
		return comb;
	}

	@SuppressWarnings("unchecked")
	private void comb1(List<T> s, List<T> prefix, int k) {
		if (comb.size() >= COMBLIMIT)
			return;

		if (s.size() < k)
			return;
		else if (k == 0) {
			comb.add(prefix);
		} else {
			LinkedList<T> sub = new LinkedList<T>();
			for (T o : s.subList(1, s.size())) {
				sub.add(o);
			}

			comb1(sub, (LinkedList<T>) ((LinkedList<T>) prefix).clone(), k);
			prefix.add(s.get(0));
			comb1(sub, prefix, k - 1);
		}
	}

	public static void main(String[] args) {
		String[] letters = { "a", "b", "c", "d", "e", "f" };
		LinkedList<String> letterslist = new LinkedList<String>();

		for (String s : letters) {
			letterslist.add(s);
		}

		Permute<String> p = new Permute<String>();

		List<List<String>> ret = p.comb1(letterslist, 3);

		System.out.println(ret);
	}

	public static <T> List<T> randomSubList(List<T> in, int size) {
		List<T> rand = new LinkedList<T>();
		rand.addAll(in);
		shuffle(rand);

		return rand.subList(0, size);
	}
}
