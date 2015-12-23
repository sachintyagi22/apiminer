package com.kb.java.parse;

public class TypeLocation {

	private String type;
	private String method;
	private Integer argNumber;
	private Integer line;
	private Integer column;
	
	public TypeLocation(String type, Integer line, Integer column) {
		this.type = type;
		this.line = line; 
		this.column = column;
	}
	
	public TypeLocation(String type, Integer line, Integer column, String method, Integer argNumber) {
		this(type, line, column);
		this.method = method;
		this.argNumber = argNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Integer getArgNumber() {
		return argNumber;
	}

	public void setArgNumber(Integer argNumber) {
		this.argNumber = argNumber;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer column) {
		this.column = column;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argNumber == null) ? 0 : argNumber.hashCode());
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
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
		TypeLocation other = (TypeLocation) obj;
		if (argNumber == null) {
			if (other.argNumber != null)
				return false;
		} else if (!argNumber.equals(other.argNumber))
			return false;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TypeLocation [type=" + type + ", method=" + method
				+ ", argNumber=" + argNumber + ", line=" + line + ", column="
				+ column + "]";
	}

}
