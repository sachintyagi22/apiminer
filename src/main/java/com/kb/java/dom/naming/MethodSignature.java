package com.kb.java.dom.naming;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.kb.java.graph.CFGraph;
import com.kb.java.parse.ClassDeclaration;

public class MethodSignature implements Serializable {

	private static final long serialVersionUID = 1L;
	private ClassDeclaration classDec;
	private Type returnType;
	private String methodName;
	private List<Declaration> parameters = new ArrayList<Declaration>();
	private CFGraph cfg;

	public MethodSignature(Type returnType, String methodName,
			List<Declaration> parameters) {
		this.returnType = returnType;
		this.methodName = methodName;
		this.parameters = parameters;
	}

	public void setCFG(CFGraph cfg) {
		this.cfg = cfg;
	}

	public void setClass(ClassDeclaration classDec) {
		this.classDec = classDec;
	}

	public CFGraph getCFG() {
		return cfg;
	}

	@Override
	public String toString() {
		return methodName;
	}
	

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classDec == null) ? 0 : classDec.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result
				+ ((returnType == null) ? 0 : returnType.hashCode());
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
		MethodSignature other = (MethodSignature) obj;
		if (classDec == null) {
			if (other.classDec != null)
				return false;
		} else if (!classDec.equals(other.classDec))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

	public List<Declaration> getParameters() {
		return parameters;
	}

}
