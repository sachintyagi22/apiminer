package com.kb.codetograph.apiminer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kb.codetograph.java.parse.JavaASTParser;
import com.kb.codetograph.java.parse.MethodInvocationResolver;
import com.kb.codetograph.java.parse.SingleClassBindingResolver;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;

public class ASTParserTest extends AbstractParseTest {

	@Test
	public void testOneMethod() {
		JavaASTParser pars = new JavaASTParser(true);
		ASTNode cu = pars.getAST(oneMethod, JavaASTParser.ParseType.COMPILATION_UNIT);
		CompilationUnit unit = (CompilationUnit) cu;

		SingleClassBindingResolver resolver = new SingleClassBindingResolver(
				unit);
		resolver.resolve();
		Map<Integer, String> typesAtPos = resolver.getVariableTypesAtPosition();

		for (Entry<Integer, String> e : typesAtPos.entrySet()) {
			Integer line = unit.getLineNumber(e.getKey());
			Integer col = unit.getColumnNumber(e.getKey());

			System.out.println(line + " , " + col + " : " + e.getValue());
		}

		for (Entry<Integer, String> e : resolver.getTypesAtPosition()
				.entrySet()) {
			Integer line = unit.getLineNumber(e.getKey());
			Integer col = unit.getColumnNumber(e.getKey());

			System.out.println("Type @ pos " + line + " , " + col + " : "
					+ e.getValue());
		}

		for (Entry<ASTNode, ASTNode> e : resolver.getVariableDependencies()
				.entrySet()) {
			ASTNode child = e.getKey();
			Integer chline = unit.getLineNumber(child.getStartPosition());
			Integer chcol = unit.getColumnNumber(child.getStartPosition());
			ASTNode parent = e.getValue();
			Integer pline = unit.getLineNumber(parent.getStartPosition());
			Integer pcol = unit.getColumnNumber(parent.getStartPosition());
			System.out.println("**** " + child + "[" + chline + ", " + chcol
					+ "] ==> " + parent.toString() + "[" + pline + ", " + pcol
					+ "]");
		}

		for (Entry<String, List<MethodInvocationResolver.MethodInvokRef>> entry : resolver
				.getMethodInvoks().entrySet()) {
			System.out.println(" ~~~~~~~~~~~ For method " + entry.getKey()
					+ " ~~~~~~~~~~~");
			for (MethodInvocationResolver.MethodInvokRef m : entry.getValue()) {
				Integer loc = m.getLocation();
				Integer line = unit.getLineNumber(loc);
				Integer col = unit.getColumnNumber(loc);
				System.out.println("[" + line + ", " + col + "] ==> " + m);
			}
		}

		for (MethodInvocationResolver.MethodDecl m : resolver.getDeclaredMethods()) {
			System.out
					.println("~~~~~~~~~~~~~~~~~ Declared Methods ~~~~~~~~~~~~~~~~~");
			System.out.println(m);
		}

		System.out.println(resolver.getVariableTypes());
	}

	
}
