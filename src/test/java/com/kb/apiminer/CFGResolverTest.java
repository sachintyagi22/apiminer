package com.kb.apiminer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;
import org.junit.Assert;
import org.junit.Test;

import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.Node;
import com.kodebeagle.javaparser.CFGResolver;
import com.kodebeagle.javaparser.JavaASTParser;
import com.kodebeagle.javaparser.JavaASTParser.ParseType;

public class CFGResolverTest extends AbstractParseTest{

	@Test
	public void testOneMethod() {

		File f = new File("/home/sachint/softwares/repos/tassal-bk/workspace/apiminer/src/test/resources/sourceJavaFiles");
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
							return /*vertex.getId() + " : " + */vertex.getLabel();
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

					FileWriter dotFileWriter = new FileWriter("diagraph.dot",true);
					
					for(DirectedGraph<Node,DirectedEdge> graph : cfgResolver.getMethodCFGs()) {
						validateGraph(graph);
						StringWriter stringWriter = new StringWriter();
						exporter.export(stringWriter, graph);

						if(stringWriter.getBuffer().toString().contains("OutputStream")){
							String graphString = stringWriter.getBuffer().toString();
							String label = graph.vertexSet().iterator().next().getLabel();
							label = StringUtils.substringBetween(label, ":", "(");
							String name = sourceFile.getName();
							name = StringUtils.substringBefore(name, ".java");
							graphString = graphString.replace("digraph G", "digraph " + name + "0" + label);
							dotFileWriter.append(graphString+"\n\n");
							dotFileWriter.flush();
						}

						stringWriter.close();
						dotFileWriter.close();
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	private void validateGraph(DirectedGraph<Node, DirectedEdge> graph) {
		Set<DirectedEdge> edgeset = graph.edgeSet();
		for(DirectedEdge e : edgeset){
			Node src = graph.getEdgeSource(e);
			Node tgt = graph.getEdgeTarget(e);
			if(src.getLabel().equals("START IF")){
				Assert.assertNotEquals(tgt.getLabel(), "END IF");
			}
		}
	}
	
}
