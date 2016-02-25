package com.kb;

import java.io.*;
import java.util.*;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DirectedGraphUnion;
import org.jgrapht.graph.builder.DirectedWeightedGraphBuilder;
import org.jgrapht.util.WeightCombiner;

import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.NamedDirectedGraph;
import com.kb.java.graph.Node;
import com.kodebeagle.javaparser.CFGResolver;
import com.kodebeagle.javaparser.JavaASTParser;

public class GraphUtils implements Serializable{
	public Map<String, String> idMap = new HashMap<>();
	public static int innerIdCounter = 0;
	public CompilationUnit cuState ;
	public List<NamedDirectedGraph> getGraphsFromFile(String fileContent, String fileName) {
		return getGraphsFromFile(fileContent, "", fileName);
	}

	public List<NamedDirectedGraph> getGraphsFromFile(String fileContent,
			String seed, String fileName) {
		CFGResolver cfgResolver = new CFGResolver(seed, fileName);
		JavaASTParser pars = new JavaASTParser(true);
		ASTNode cu = pars.getAST(fileContent,
				JavaASTParser.ParseType.COMPILATION_UNIT);
		cu.accept(cfgResolver);
		cuState = ((CompilationUnit) cu);
		List<NamedDirectedGraph> graphs = cfgResolver.getMethodCFGs();
		return graphs;
	}

	public void trim(NamedDirectedGraph current, double support) {
		Set<DirectedEdge> edgesToRemove = new HashSet<>();
		for (DirectedEdge e : current.edgeSet()) {
			if (e.getWeight() < support) {
				edgesToRemove.add(e);
			}
		}

		for (DirectedEdge e : edgesToRemove) {
			current.removeEdge(e);
		}

		Set<Node> nodesToRemove = new HashSet<>();
		for (Node n : current.vertexSet()) {
			if (current.inDegreeOf(n) == 0 && current.outDegreeOf(n) == 0) {
				nodesToRemove.add(n);
			}
		}
		for (Node n : nodesToRemove) {
			current.removeVertex(n);
		}
	}
	
	
	 public NamedDirectedGraph mergeGraphs(NamedDirectedGraph digraph1, NamedDirectedGraph digraph2) {
	        DirectedGraphUnion<Node, DirectedEdge> du = new DirectedGraphUnion<>(digraph1, digraph2, WeightCombiner.SUM);
	        DirectedWeightedGraphBuilder<Node, DirectedEdge, NamedDirectedGraph> dwgb = new DirectedWeightedGraphBuilder<Node, DirectedEdge, NamedDirectedGraph>(new NamedDirectedGraph());

	        for(DirectedEdge e : du.edgeSet()){
	            Node src = e.getSource();
	            Node target = e.getTarget();
	            double w = du.getEdgeWeight(e);
	            dwgb.addEdge(src, target, w);
	        }
	        return dwgb.build();
	 }

	public void getNamedDirectedGraphs(Map<String, NamedDirectedGraph> instances, List<NamedDirectedGraph> graphs) {
		for (NamedDirectedGraph g : graphs) {
			int startLineNumber = cuState.getLineNumber(g.getStartLineNumber());
			int endLineNumber = cuState.getLineNumber(g.getEndLineNumber());
			instances.put(
					g.getId(),
					new NamedDirectedGraph(g, g.getId(), g.getLabel(), g
							.getSeedName(), g.getMethodName(), g.getFileName(),
							g.getParamTypes(),startLineNumber,endLineNumber));
		}
	}

	public void saveToFile(NamedDirectedGraph current, String path)
			throws FileNotFoundException {
		DOTExporter<Node, DirectedEdge> exporter = new DOTExporter<>(vertexIdProvider, vertexNameProvider, edgeEdgeNameProvider);
		File f = new File(path);
		if(f.exists()){
			f.delete();
		}
		exporter.export(new PrintWriter(new FileOutputStream(f)), current);
		System.out.println("######## this id to check idmap  " + idMap);
	}

	public String saveToString(NamedDirectedGraph current)
			throws FileNotFoundException {
		StringWriter writer = new StringWriter();
		DOTExporter<Node, DirectedEdge> exporter = new DOTExporter<>(vertexIdProvider, vertexNameProvider, edgeEdgeNameProvider);
		exporter.export(writer, current);
		System.out.println("######## this id to check idmap  "+idMap);
		return writer.getBuffer().toString();
	}
	private static VertexNameProvider<Node> vertexNameProvider = new VertexNameProvider<Node>() {
		@Override
		public String getVertexName(Node vertex) {
			return vertex.getLabel();
		}
	};

	private VertexNameProvider<Node> vertexIdProvider = new VertexNameProvider<Node>() {
		@Override
		public String getVertexName(Node vertex) {
			String id = idMap.get(vertex.getLabel());
			if(id == null){
				id = String.valueOf(innerIdCounter++);
				idMap.put(vertex.getLabel(), id);
			}
			return id;
		}
	};

	public List<String> getTopN(TreeMultiset<String> seedNames, int max) {
		List<String> elems = new ArrayList<String>(max);
		int i = 0;
		ImmutableMultiset<String> highestCountFirst = Multisets.copyHighestCountFirst(seedNames);

		for(Multiset.Entry<String> e : highestCountFirst.entrySet()){
			if(i > max) break;
			i ++;
			elems.add(e.getElement());
		}
		return elems;
	}

	public static EdgeNameProvider<DirectedEdge> edgeEdgeNameProvider = new EdgeNameProvider<DirectedEdge>() {
		@Override
		public String getEdgeName(DirectedEdge edge) {
			return String.valueOf(edge.getWeight());
		}
	};
}
