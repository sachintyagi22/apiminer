package com.kodebeagle.apiminer;

import java.io.*;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
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

		File f = new File("./sourceJavaFiles");
		File[] listOfSourceFiles = f.listFiles();


		if(listOfSourceFiles != null && listOfSourceFiles.length > 0){

			for(File sourceFile :  listOfSourceFiles){
				try{

					System.out.println("File :: " + sourceFile);

					FileInputStream fileInputStream = new FileInputStream(sourceFile);
					String fileContent = readInputStream(fileInputStream);

					CFGResolver cfgResolver = new CFGResolver();
					JavaASTParser pars = new JavaASTParser(true);
					ASTNode cu = pars.getAST(fileContent, ParseType.COMPILATION_UNIT);
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
					//exporter.export(new OutputStreamWriter(System.out), cfgResolver.getMethodCFGs().get(0));

					FileWriter dotFileWriter = new FileWriter("diagraph.dot",true);
					for(DirectedGraph<Node,DefaultEdge> graphs : cfgResolver.getMethodCFGs()) {
						StringWriter stringWriter = new StringWriter();
						exporter.export(stringWriter, graphs);

						if(stringWriter.toString().contains("BufferedReader")){
							dotFileWriter.append(stringWriter.toString()+"\n\n");
						}

						stringWriter.close();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
