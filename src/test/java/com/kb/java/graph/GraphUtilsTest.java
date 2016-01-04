package com.kb.java.graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kb.java.dom.expression.UnknownExpressionException;
import com.kb.java.dom.naming.MethodSignature;
import com.kb.java.parse.CFGParser;
import com.kb.java.parse.ClassDeclaration;
import com.kb.java.parse.TypeLocation;

public class GraphUtilsTest {
	
	private String testClass;
	private String oneMethod;

	@Before
	public void setUp() {
		InputStream is = this.getClass().getResourceAsStream("/Test.java");
		testClass = readInputStream(is);
		oneMethod = readInputStream(this.getClass().getResourceAsStream(
				"/OneMethod.java"));
	}
	
	@Test
	public void testOneMethod() throws UnknownExpressionException {
		CFGParser cfgParser = new CFGParser();
		List<ClassDeclaration> cfg = cfgParser.parse(oneMethod);
		// Check only one class is found
		Assert.assertTrue(cfg.size() == 1);

		ClassDeclaration cd = cfg.get(0);
		List<MethodSignature> methods = cd.getMethods();
		Assert.assertTrue(methods.size() == 1);
		MethodSignature method = methods.get(0);
		CFGraph methodCFG = method.getCFG();
		CFGraphUtils utils = new CFGraphUtils();
		String dot = utils.toDot(methodCFG);
		System.out.println(dot);
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
