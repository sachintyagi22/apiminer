package com.kb;

import java.io.*;
import java.util.*;

import org.eclipse.jdt.core.dom.ASTNode;
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
	public int innerIdCounter = 0;
	public List<NamedDirectedGraph> getGraphsFromFile(String fileContent) {
		return getGraphsFromFile(fileContent, "");
	}

	public List<NamedDirectedGraph> getGraphsFromFile(String fileContent,
			String seed) {
		CFGResolver cfgResolver = new CFGResolver(seed);
		JavaASTParser pars = new JavaASTParser(true);
		ASTNode cu = pars.getAST(fileContent,
				JavaASTParser.ParseType.COMPILATION_UNIT);
		cu.accept(cfgResolver);

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
			instances.put(
					g.getId(),
					new NamedDirectedGraph(g, g.getId(), g.getLabel(), g
							.getSeedName(), g.getMethodName(), g
							.getParamTypes()));
//				i++;
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
	}

	public String saveToString(NamedDirectedGraph current, String path)
			throws FileNotFoundException {
		StringWriter writer = new StringWriter();
		DOTExporter<Node, DirectedEdge> exporter = new DOTExporter<>(vertexIdProvider, vertexNameProvider, edgeEdgeNameProvider);
		exporter.export(new PrintWriter(writer), current);
		return writer.toString();
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

	public static EdgeNameProvider<DirectedEdge> edgeEdgeNameProvider = new EdgeNameProvider<DirectedEdge>() {
		@Override
		public String getEdgeName(DirectedEdge edge) {
			return String.valueOf(edge.getWeight());
		}
	};
}
