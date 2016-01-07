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

	String something(){
		System.out.println("Something callled...");
		return "something";
	}
	
	void method(String s){
		System.out.println("method called..");
	}
	
	CFGResolverTest getInstance(){
		System.out.println("get instance called...");
		return this;
	}
	
	
	@Test
	public void testOneMethod() {
		
		CFGResolverTest test = new CFGResolverTest();
		test.getInstance().method(something());
		
		CFGResolver cfgResolver = new CFGResolver();
		JavaASTParser pars = new JavaASTParser(true);
		ASTNode cu = pars.getAST(testClass/*oneMethod*/, ParseType.COMPILATION_UNIT);
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
