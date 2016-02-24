package com.kb.apiminer;

import org.junit.Test;

public class ASTParserTest extends AbstractParseTest {

	@Test
	public void testOneMethod() {/*
		JavaASTParser pars = new JavaASTParser(true);
		ASTNode cu = pars.getAST(oneMethod, ParseType.COMPILATION_UNIT);
		CompilationUnit unit = (CompilationUnit) cu;

		com.kodebeagle.javaparser.SingleClassBindingResolver resolver = new com.kodebeagle.javaparser.SingleClassBindingResolver(
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

		for (Entry<String, List<MethodInvokRef>> entry : resolver
				.getMethodInvoks().entrySet()) {
			System.out.println(" ~~~~~~~~~~~ For method " + entry.getKey()
					+ " ~~~~~~~~~~~");
			for (MethodInvokRef m : entry.getValue()) {
				Integer loc = m.getLocation();
				Integer line = unit.getLineNumber(loc);
				Integer col = unit.getColumnNumber(loc);
				System.out.println("[" + line + ", " + col + "] ==> " + m);
			}
		}

		for (MethodDecl m : resolver.getDeclaredMethods()) {
			System.out
					.println("~~~~~~~~~~~~~~~~~ Declared Methods ~~~~~~~~~~~~~~~~~");
			System.out.println(m);
		}

		System.out.println(resolver.getVariableTypes());
	*/}

	
}
