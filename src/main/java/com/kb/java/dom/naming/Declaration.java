package com.kb.java.dom.naming;

import java.io.Serializable;

import com.kb.java.dom.expression.Variable;

public class Declaration implements Serializable {
	
	private static final long serialVersionUID = 1L;
	Type type;
	Variable name;

	public Declaration(Type type, Variable name) {
		this.type = type;
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public Variable getVariable() {
		return name;
	}

	@Override
	public String toString() {
		if (name != null)
			return type + " " + name;
		else
			return type + " " + type.toString().toLowerCase().charAt(0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Declaration other = (Declaration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
