package com.kodebeagle.apiminer;

import java.io.*;
import java.util.List;

import com.kb.java.graph.DirectedEdge;
import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.junit.Test;

import com.kb.java.graph.Node;
import com.kb.java.parse.CFGResolver;
import com.kb.java.parse.JavaASTParser;
import com.kb.java.parse.JavaASTParser.ParseType;

public class CFGResolverTest extends AbstractParseTest{

	@Test
	public void testOneMethod() {

		File f = new File("./fileniochannels");
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
					DOTExporter<Node, DirectedEdge> exporter = new DOTExporter<Node, DirectedEdge>(vertexIdProvider, vertexNameProvider , null);
					//exporter.export(new OutputStreamWriter(System.out), cfgResolver.getMethodCFGs().get(0));

					FileWriter dotFileWriter = new FileWriter("diagraph2.dot",true);
					for(DirectedGraph<Node,DirectedEdge> graph : cfgResolver.getMethodCFGs()) {
						StringWriter stringWriter = new StringWriter();
						graph.vertexSet();
						graph.edgeSet();
						exporter.export(stringWriter, graph);

						if(stringWriter.getBuffer().toString().contains("FileChannel")){
							dotFileWriter.append(stringWriter.getBuffer().toString()+"\n\n");
							dotFileWriter.flush();
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
