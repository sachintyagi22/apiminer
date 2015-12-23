package com.kb.utils;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Tools {

	public static File getSubDir(File dir, String sub) {
		String parent = dir.getPath();

		if (!parent.endsWith("/")) {
			parent = parent + "/";
		}

		parent = parent + sub;

		return makeDir(parent);
	}

	public static File makeDir(String s) {
		File wd = new File(s);

		if (!wd.exists()) {
			System.out.println("creating dir: " + wd);
			wd.mkdir();
		}

		return wd;
	}

	public static String makeNameSimple(String name) {
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public static void printTime() {
		System.out.println("TIMESTAMP: " + new Date());
	}

	public static Collection<String> makeCollection(String... ks) {
		LinkedList<String> list = new LinkedList<String>();

		for (String s : ks) {
			list.add(s);
		}

		return list;

	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> makeCollection(T... ks) {
		LinkedList<T> list = new LinkedList<T>();

		for (T s : ks) {
			list.add(s);
		}

		return list;

	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> makeList(T... ks) {
		LinkedList<T> list = new LinkedList<T>();

		for (T s : ks) {
			list.add(s);
		}

		return list;
	}

	public static List<String> makeListFromString(String s) {
		if (s == null)
			return new LinkedList<String>();

		LinkedList<String> list = new LinkedList<String>();

		for (String str : s.split(",")) {
			list.add(str.trim());
		}

		return list;
	}

	public static int countLines(String s) {
		return s.split("\n").length;
	}

	public static List<File> ls(File dir) {
		LinkedList<File> files = new LinkedList<File>();

		if (!dir.isDirectory())
			return files;

		for (File f : dir.listFiles()) {
			files.add(f);
		}

		return files;
	}

	public static <T> String makeStringFromList(Collection<T> list) {
		StringBuffer ret = new StringBuffer();

		for (T o : list) {
			ret.append(o.toString() + ", ");
		}

		return "[ " + ret.toString() + " ]";
	}

}
