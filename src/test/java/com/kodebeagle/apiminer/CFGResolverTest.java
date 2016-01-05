package com.kodebeagle.apiminer;

import java.io.OutputStreamWriter;

import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import com.kb.java.graph.Node;
import com.kb.java.parse.CFGResolver;
import com.kb.java.parse.JavaASTParser;
import com.kb.java.parse.JavaASTParser.ParseType;

public class CFGResolverTest extends AbstractParseTest{

	@Test
	public void testOneMethod() {
		CFGResolver cfgResolver = new CFGResolver();
		JavaASTParser pars = new JavaASTParser(true);
		ASTNode cu = pars.getAST(oneMethod, ParseType.COMPILATION_UNIT);
		cu.accept(cfgResolver);
		VertexNameProvider<Node> vertexNameProvider = new VertexNameProvider<Node>() {
			@Override
			public String getVertexName(Node vertex) {
				return vertex.getId() + " : " + vertex.getLabel();
			}
		};
		
		VertexNameProvider<Node> vertexIdProvider = new VertexNameProvider<Node>() {
			@Override
			public String getVertexName(Node vertex) {
				return String.valueOf(vertex.getId());
			}
		};
		DOTExporter<Node, DefaultEdge> exporter = new DOTExporter<Node, DefaultEdge>(vertexIdProvider, vertexNameProvider , null);
		
		exporter.export(new OutputStreamWriter(System.out), cfgResolver.getBaseGraph());
	}
}
