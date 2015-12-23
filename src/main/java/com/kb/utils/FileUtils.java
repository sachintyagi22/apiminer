package com.kb.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	/**
	 * Read the file as a String.
	 * @param file
	 * @return
	 */
	public String readFile(File file) {
		BufferedReader br = null;
		StringBuffer contents = new StringBuffer();
		try {
			br = new BufferedReader(new java.io.FileReader(file));
			while (br.ready()) {
				contents.append(br.readLine() + "\n");
			}
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			System.err.println("ioexception: " + e);
			return "";
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return contents.toString();

	}

	/**
	 * Get all files.
	 * 
	 * @param directory
	 * @return
	 */
	public List<File> findAll(File directory, FileFilter filter) {
		File[] javaFiles = directory.listFiles(filter);
		File[] directories = directory.listFiles(new DirectoryFilter());

		List<File> srcs = new ArrayList<File>();

		if (javaFiles != null) {
			for (File f : javaFiles) {
				srcs.add(f);
			}
		}

		if (directories != null) {
			for (File d : directories) {
				srcs.addAll(findAll(d, filter));
			}
		}

		return srcs;
	}

	public List<File> findAll(File directory, String exn) {
		return findAll(directory, new FileExtensionFilter(exn));
	}

	public List<File> findFile(File searchdirectory, String startswith,
			String endswith) {
		return findAll(searchdirectory, new FileStartEndFilter(startswith,
				endswith));
	}

	
	/**
	 * Different filters used.
	 * @author sachint
	 *
	 */
	private class DirectoryFilter implements FileFilter {

		public boolean accept(File f) {
			return f.isDirectory();
		}
	}

	private class FileExtensionFilter implements FileFilter {
		String exn = ".java";

		public FileExtensionFilter(String exn) {
			this.exn = "." + exn;
		}

		public boolean accept(File f) {
			return f.getName().endsWith(exn);
		}
	}

	private class FileStartEndFilter implements FileFilter {
		String start, end;

		public FileStartEndFilter(String start, String end) {
			this.start = start;
			this.end = end;
		}

		public boolean accept(File f) {
			return f.getName().startsWith(start) && f.getName().endsWith(end);
		}
	}

}
