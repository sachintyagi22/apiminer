package com.kb.java.graph;


public class SeedCreationNode implements Node {

	private SeedCreationType seedCreationType;
	private String type;
	private String varName;
	private InvocationNode invocationDelegate;
	private int id;
	
	public enum SeedCreationType{
		PARAM, INIT, METHOD;
	}
	
	public SeedCreationNode(int id, String varName, String type, SeedCreationType createionType) {
		if(createionType == SeedCreationType.METHOD){
			throw new IllegalArgumentException("No delegate supplied");
		}
		this.id = id;
		this.varName = varName;
		this.seedCreationType = createionType;
		this.type = type;
	}
	
	public SeedCreationNode(int id, String varName, SeedCreationType creationType, InvocationNode delegate) {
		this.id = id;
		this.varName = varName;
		this.seedCreationType = creationType;
		this.invocationDelegate = delegate;
	}
	
	public SeedCreationType getSeedCreationType() {
		return seedCreationType;
	}

	public void setSeedCreationType(SeedCreationType seedCreationType) {
		this.seedCreationType = seedCreationType;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public InvocationNode getInvocationDelegate() {
		return invocationDelegate;
	}

	public void setInvocationDelegate(InvocationNode invocationDelegate) {
		this.invocationDelegate = invocationDelegate;
	}

	@Override
	public String getLabel() {
		if (seedCreationType == SeedCreationType.METHOD){
			return invocationDelegate.getLabel();
		} else if (seedCreationType == SeedCreationType.PARAM){
			return getType() + "<param>()";
		} else if (seedCreationType == SeedCreationType.INIT){
			return getType() + "<init>()";
		}
		return null;
	}
	
	public String getType() {
		if(seedCreationType == SeedCreationType.METHOD){
			invocationDelegate.getTargetType();
		}
		return type;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((invocationDelegate == null) ? 0 : invocationDelegate
						.hashCode());
		result = prime
				* result
				+ ((seedCreationType == null) ? 0 : seedCreationType.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SeedCreationNode other = (SeedCreationNode) obj;
		if (invocationDelegate == null) {
			if (other.invocationDelegate != null)
				return false;
		} else if (!invocationDelegate.equals(other.invocationDelegate))
			return false;
		if (seedCreationType != other.seedCreationType)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}




}
