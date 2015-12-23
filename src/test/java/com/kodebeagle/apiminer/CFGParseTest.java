package com.kodebeagle.apiminer;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.kb.java.dom.expression.UnknownExpressionException;
import com.kb.java.dom.naming.MethodSignature;
import com.kb.java.parse.CFGParser;
import com.kb.java.parse.ClassDeclaration;
import com.kb.java.parse.TypeLocation;

/**
 * Unit test for simple App.
 */
public class CFGParseTest {

	private String testClass;
	private String oneMethod;

	@Before
	public void setUp() {
		InputStream is = this.getClass().getResourceAsStream("/Test.java");
		testClass = readInputStream(is);
		oneMethod = readInputStream(this.getClass().getResourceAsStream(
				"/OneMethod.java"));
	}

	//@Test
	public void testCFGParsing() {
		CFGParser cfgParser = new CFGParser();
		try {
			List<ClassDeclaration> cfg = cfgParser.parse(testClass);
			// Check only one class is found
			Assert.assertTrue(cfg.size() == 1);

			List<MethodSignature> methods = cfg.get(0).getMethods();
			Assert.assertTrue(methods.size() == 16);

			MethodSignature method = null;

			for (MethodSignature m : methods) {
				if (m.getMethodName().equals("handleResponse")) {
					method = m;
				}
			}

			Assert.assertTrue(method != null);
			System.out.println(method.getCFG().getMethodSig());

			System.out.println("******************* \n ");

			System.out.println(method.getCFG());

		} catch (UnknownExpressionException e) {
			fail();
		}
	}

	@Test
	public void testOneMethod() throws UnknownExpressionException {
		CFGParser cfgParser = new CFGParser();
		List<ClassDeclaration> cfg = cfgParser.parse(oneMethod);
		// Check only one class is found
		Assert.assertTrue(cfg.size() == 1);

		ClassDeclaration cd = cfg.get(0);
		Set<TypeLocation> typeLocs = cd.getTypeLocations();
		for(TypeLocation type: typeLocs){
			System.out.println(type);
		}
		System.out.println("************* Methods *************** ");
		List<MethodSignature> methods = cd.getMethods();
		Assert.assertTrue(methods.size() == 1);
		MethodSignature method = methods.get(0);
		
		System.out.println(method.getCFG());
	}

	@After
	public void after() {
		System.out.println("after");
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
