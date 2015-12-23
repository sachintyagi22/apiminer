package com.kb.java.graph;


public class CFGraphUtils {

	/*public String toDot(CFGraph graph) {
		if (graph.getEntry() == null)
			return "Empty Method: " + graph.getMethodSig().getName();

		StringBuffer br = new StringBuffer();

		br.append("digraph " + graph.getMethodSig().getName() + " {\n");

		CFNode current = graph.getEntry();

		br.append(printTransitions(current));

		br.append("\n}\n");

		return br.toString();

	}*/

	///private final Set<CFNode> printed = new HashSet<CFNode>();

	/*private String printTransitions(CFNode current) {
		printed.add(current);

		// System.out.println("Sucessors of " + current + " = " +
		// current.getSuccessors());

		StringBuffer br = new StringBuffer();

		for (CFNode child : current.getSuccessors()) {
			if (child == null) {
				continue;
			}

			br.append(escapeNode(current) + " -> " + escapeNode(child) + ";\n");

			if (!printed.contains(child)) {
				br.append(printTransitions(child));
			}
		}

		return br.toString();
	}*/

	/*private String escapeNode(CFNode input) {
		return "\"" + StringEscapeUtils.escapeJava(input.toString()) + "\"";
	}*/

}
