package com.kb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.graph.DirectedGraphUnion;
import org.jgrapht.graph.builder.DirectedWeightedGraphBuilder;
import org.jgrapht.util.WeightCombiner;

import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.NamedDirectedGraph;
import com.kb.java.graph.Node;
import com.kodebeagle.javaparser.CFGResolver;
import com.kodebeagle.javaparser.JavaASTParser;

public class GraphUtils {

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
}
