package com.kb.apiminer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;

public abstract class AbstractParseTest {



	protected String testClass;
	protected String oneMethod;

	@Before
	public void setUp() {
		InputStream is = this.getClass().getResourceAsStream("/Test.java");
		testClass = readInputStream(is);
		oneMethod = readInputStream(this.getClass().getResourceAsStream(
				"/OneMethod.java"));
	}

	protected String readInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuffer contents = new StringBuffer();
		try {
			br = new BufferedReader(new java.io.InputStreamReader(is));
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
				e.printStackTrace();
			}
		}
		return contents.toString();

	}


}
