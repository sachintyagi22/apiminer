package com.kb.java.graph;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;

import com.kb.java.model.Clusterer;

public class NamedDirectedGraph implements DirectedGraph<Node, DirectedEdge>{
	
	private DirectedGraph<Node, DirectedEdge> delegate;
	private final String id;
	private final String label;
	
	public NamedDirectedGraph(DirectedGraph<Node, DirectedEdge> delegate, String id, String label) {
		this.delegate = delegate;
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer(label).append(" [ ");
		for(Node n : delegate.vertexSet()){
			if(n.getLabel().contains(Clusterer.FILE_CHANNEL))
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
		return delegate.getEdgeWeight(e);
	}
	
	
}
