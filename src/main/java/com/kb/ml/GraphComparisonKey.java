package com.kb.ml;

public class GraphComparisonKey {
	
	final private String src;
	final private String tgt;
	private int hashCode = -1;

	public GraphComparisonKey(String src, String tgt) {
		if(src == null || tgt == null){
			throw new IllegalArgumentException("No nulls please");
		}
		this.src = src;
		this.tgt = tgt;
	}
	
	@Override
	public int hashCode() {
		if (hashCode == -1)
			hashCode = 31 * src.hashCode() * tgt.hashCode();
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphComparisonKey other = (GraphComparisonKey) obj;
		if (src == null) {
			if (other.src != null)
				return false;
		} 
		if (tgt == null) {
			if (other.tgt != null)
				return false;
		} 
		
		if ((!tgt.equals(other.tgt) && !tgt.equals(other.src)))
			return false;
		
		if ((!src.equals(other.tgt) && !src.equals(other.src)))
			return false;
		return true;
	}
}
