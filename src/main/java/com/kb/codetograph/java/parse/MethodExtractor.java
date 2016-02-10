package com.kb.codetograph.java.parse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodExtractor extends ASTVisitor {
	
	private List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

	public List<MethodDeclaration> getMethods(ASTNode node) {
		node.accept(this);
		return methods;
	}

	@Override
	public boolean visit(MethodDeclaration md) {
		methods.add(md);
		return false;
	}

}
