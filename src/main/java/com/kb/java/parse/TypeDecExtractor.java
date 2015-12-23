package com.kb.java.parse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TypeDecExtractor extends ASTVisitor {
	
	private List<TypeDeclaration> classes = new ArrayList<TypeDeclaration>();

	public List<TypeDeclaration> getTypeDefs(ASTNode node) {
		node.accept(this);
		return classes;
	}

	@Override
	public boolean visit(TypeDeclaration td) {
		classes.add(td);
		return false;
	}

}
