package com.kb.java.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class InvocationNode extends LabelNode{

	private String targetType;
	private String methodName;
	private List<String> argTypes = new ArrayList<String>();
	
	public InvocationNode(int id, String targetType, String methodName,
			List<String> argTypes) {
		super(id, "");
		this.targetType = targetType;
		this.methodName = methodName;
		this.argTypes = argTypes;
	}

	@Override
	public String getLabel() {
		StringBuffer sb = new StringBuffer();
		if(!StringUtils.isEmpty(targetType)){
			sb.append(targetType).append(".");
		}
		sb.append(methodName);
		sb.append("()#").append(argTypes.size());
		return sb.toString();
	}
	
	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<String> getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(List<String> argTypes) {
		this.argTypes = argTypes;
	}

	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argTypes == null) ? 0 : argTypes.hashCode());
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result
				+ ((targetType == null) ? 0 : targetType.hashCode());
		return result;
	}*/

	/*@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InvocationNode other = (InvocationNode) obj;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (targetType == null) {
			if (other.targetType != null)
				return false;
		} else if (!targetType.equals(other.targetType))
			return false;
		if (argTypes == null) {
			if (other.argTypes != null)
				return false;
		} else if (!argTypes.equals(other.argTypes))
			return false;
		return true;
	}*/

	public boolean equals(Object pInvocationNode) {
		return super.equals(pInvocationNode);
	}

	public int hashCode() {
		return super.hashCode();
	}

}
