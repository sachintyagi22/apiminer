package com.kb.java.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

public class NamedDirectedGraph implements DirectedGraph<Node, DirectedEdge>, WeightedGraph<Node, DirectedEdge>,Serializable {
	
	private DirectedGraph<Node, DirectedEdge> delegate;
	private final String seedName;
	private final String methodName;

	public String getFileName() {
		return fileName;
	}

	private final String fileName;
	private final String id;
	private final String label;
	private final Set<String> paramTypes;
	private int startLineNumber;
	private int endLineNumber;
	
	public NamedDirectedGraph(DirectedGraph<Node, DirectedEdge> delegate, String id, String label, String seedName,
							  String methodName, String fileName, Set<String> paramTypes, int startLineNumber, int endLineNumber)  {
		this.delegate = delegate;
		this.id = id;
		this.label = label;
		this.seedName = seedName;
		this.methodName = methodName;
		this.fileName = fileName;
		this.paramTypes = paramTypes;
		this.startLineNumber = startLineNumber;
		this.endLineNumber = endLineNumber;
	}

	public NamedDirectedGraph() {
		this.id="";
		this.label="";
		this.seedName = "";
		this.methodName = "";
		this.fileName = "";
		this.delegate= new DefaultDirectedGraph<Node, DirectedEdge>(DirectedEdge.class);
		this.paramTypes = Collections.emptySet();
	}

	public Set<String> getParamTypes() {
		return paramTypes;
	}

	public String getSeedName() {
		return seedName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}

	public int getStartLineNumber() { return startLineNumber; }

	public int getEndLineNumber() { return endLineNumber; }

	public List<Node> getAsList(){
		Set<Node> vertexSet = vertexSet();
		List<Node> nodeList = new ArrayList<Node>(vertexSet.size());
		Node root = null;
		for(Node n : vertexSet){
			if(inDegreeOf(n) == 0) {
				root = n;
				nodeList.add(n);
				break;
			}
		}
		
		traverseFrom(root, nodeList);
		return nodeList;
	}
	
	private void traverseFrom(Node root, List<Node> nodeList) {
		Set<DirectedEdge> outs = outgoingEdgesOf(root);
		for(DirectedEdge o : outs){
			Node next = o.getTarget();
			if(nodeList.contains(next)){
				//throw new IllegalStateException("Cycle detected");
				continue;
			}
			nodeList.add(next);
			traverseFrom(next, nodeList);
		}
	}

	@Override
	public String toString() {
		StringBuffer b = new StringBuffer("x " + label+" y").append(" [ ");
		for(Node n : delegate.vertexSet()){
				b.append(n.getLabel() + " , ");
		}
		b.append(" ]");
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
		//result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NamedDirectedGraph other = (NamedDirectedGraph) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}



	public int inDegreeOf(Node vertex) {
		return delegate.inDegreeOf(vertex);
	}

	public Set<DirectedEdge> incomingEdgesOf(Node vertex) {
		return delegate.incomingEdgesOf(vertex);
	}

	public int outDegreeOf(Node vertex) {
		return delegate.outDegreeOf(vertex);
	}

	public Set<DirectedEdge> outgoingEdgesOf(Node vertex) {
		return delegate.outgoingEdgesOf(vertex);
	}

	public Set<DirectedEdge> getAllEdges(Node sourceVertex, Node targetVertex) {
		return delegate.getAllEdges(sourceVertex, targetVertex);
	}

	public DirectedEdge getEdge(Node sourceVertex, Node targetVertex) {
		return delegate.getEdge(sourceVertex, targetVertex);
	}

	public EdgeFactory<Node, DirectedEdge> getEdgeFactory() {
		return delegate.getEdgeFactory();
	}

	public DirectedEdge addEdge(Node sourceVertex, Node targetVertex) {
		return delegate.addEdge(sourceVertex, targetVertex);
	}

	public boolean addEdge(Node sourceVertex, Node targetVertex, DirectedEdge e) {
		return delegate.addEdge(sourceVertex, targetVertex, e);
	}

	public boolean addVertex(Node v) {
		return delegate.addVertex(v);
	}

	public boolean containsEdge(Node sourceVertex, Node targetVertex) {
		return delegate.containsEdge(sourceVertex, targetVertex);
	}

	public boolean containsEdge(DirectedEdge e) {
		return delegate.containsEdge(e);
	}

	public boolean containsVertex(Node v) {
		return delegate.containsVertex(v);
	}

	public Set<DirectedEdge> edgeSet() {
		return delegate.edgeSet();
	}

	public Set<DirectedEdge> edgesOf(Node vertex) {
		return delegate.edgesOf(vertex);
	}

	public boolean removeAllEdges(Collection<? extends DirectedEdge> edges) {
		return delegate.removeAllEdges(edges);
	}

	public Set<DirectedEdge> removeAllEdges(Node sourceVertex, Node targetVertex) {
		return delegate.removeAllEdges(sourceVertex, targetVertex);
	}

	public boolean removeAllVertices(Collection<? extends Node> vertices) {
		return delegate.removeAllVertices(vertices);
	}

	public DirectedEdge removeEdge(Node sourceVertex, Node targetVertex) {
		return delegate.removeEdge(sourceVertex, targetVertex);
	}

	public boolean removeEdge(DirectedEdge e) {
		return delegate.removeEdge(e);
	}

	public boolean removeVertex(Node v) {
		return delegate.removeVertex(v);
	}

	public Set<Node> vertexSet() {
		return delegate.vertexSet();
	}

	public Node getEdgeSource(DirectedEdge e) {
		return delegate.getEdgeSource(e);
	}

	public Node getEdgeTarget(DirectedEdge e) {
		return delegate.getEdgeTarget(e);
	}

	public double getEdgeWeight(DirectedEdge e) {
		DirectedEdge edge = getEdge(e.getSource(), e.getTarget());
		return edge.getWeight();
	}


	@Override
	public void setEdgeWeight(DirectedEdge directedEdge, double weight) {
		directedEdge.setWeight(weight);
	}
}
