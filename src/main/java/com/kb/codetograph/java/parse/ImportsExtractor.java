package com.kb.codetograph.java.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;

public class ImportsExtractor extends ASTVisitor {

	private Map<String, String> typeMap = new HashMap<String, String>();
	private String pkg;

	public Map<String, String> getImports(ASTNode node) {
		node.accept(this);
		return typeMap;
	}

	/**
	 * TODO: Should this be here? Either rename or remove.
	 * @return
	 */
	public String getPkg() {
		return pkg;
	}

	@Override
	public boolean visit(PackageDeclaration node) {
		pkg = node.getName().toString();
		return true;
	}
	
	@Override
	public boolean visit(ImportDeclaration node) {
		Name name = node.getName();
		String fullType = name.toString();
		StringTokenizer tokenizer = new StringTokenizer(fullType, ".");
		String shortType = null;
		while(tokenizer.hasMoreTokens()){
			shortType = tokenizer.nextToken();
		}
		typeMap.put(shortType, fullType);
		return true;
	}
}
