package com.kb.java.parse;

import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;

public class CFGResolver extends MethodInvocationResolver{

	@Override
	public boolean visit(IfStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	@Override
	public boolean visit(EnhancedForStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ForStatement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
}
