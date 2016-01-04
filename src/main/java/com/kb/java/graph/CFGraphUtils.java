package com.kb.java.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

public class CFGraphUtils {

	private int i = 0;
	private Map<String, Integer> labelVsIdMap = new HashMap<String, Integer>();
	private Set<String> edgeSet = new HashSet<String>();
	private Set<String> vertexSet = new HashSet<String>();

	public String toDot(CFGraph graph) {
		if (graph.getEntry() == null)
			return "Empty Method: " + graph.getMethodSig().getName();

		StringBuffer br = new StringBuffer();

		br.append("digraph " + graph.getMethodSig().getName() + " {\n");

		CFNode current = graph.getEntry();

		printTransitions(current);
		for(String v : vertexSet){
			br.append(v + "\n");
		}
		for(String e : edgeSet){
			br.append(e);
		}

		br.append("\n}\n");

		return br.toString();

	}

	private final Set<CFNode> printed = new HashSet<CFNode>();

	private void printTransitions(CFNode current) {
		
		printed.add(current);

		if (current.getSubgraphs() != null && !current.getSubgraphs().isEmpty()) {
			for (CFNode branch : current.getSubgraphs()) {
				if (branch == null) {
					continue;
				}
				edgeSet.add(getLabelId(current) + " -> " + getLabelId(branch) + ";\n");
				if (!printed.contains(branch)) {
					printTransitions(branch);
				}
			}
		}
		CFNode child = current.getNext();
		if (child != null) {

			edgeSet.add(getLabelId(current) + " -> " + getLabelId(child) + ";\n");

			if (!printed.contains(child)) {
				printTransitions(child);
			}
		}

	}

	private int getLabelId(CFNode node) {
		String currentLabel = escapeNode(node);
		if(labelVsIdMap.get(currentLabel) == null){
			labelVsIdMap.put(currentLabel, i++);
		}
		int currLabelId = labelVsIdMap.get(currentLabel);
		String shape = "box";
		if(node.getSubgraphs()!=null){
			shape="diamond";
		}
		vertexSet.add(currLabelId + " [label=" + currentLabel + " shape="+shape+" style=rounded]");
		return currLabelId;
	}

	private String escapeNode(CFNode input) {
		return "\"" + StringEscapeUtils.escapeJava(input.getLabel()) + "\"";
	}

}
