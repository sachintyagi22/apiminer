package com.kb.miner;

import java.util.Set;

import com.google.common.collect.Sets;
import com.kb.java.dom.naming.MethodSignature;
import com.kb.java.dom.naming.Type;
import com.kb.java.dom.statement.Statement;
import com.kb.java.dom.statement.VariableDeclarationStatement;
import com.kb.java.graph.CFGraph;
import com.kb.java.graph.CFNode;
import com.kb.java.graph.StatementNode;
import com.kb.java.parse.ClassDeclaration;

public class Util {

	static Set<Type> getTypesUsed(ClassDeclaration dec) {
		Set<Type> ret = Sets.newHashSet();

		for (MethodSignature method : dec.getMethods()) {
			ret.addAll(Util.getTypesUsed(method.getCFG()));
		}

		return ret;
	}

	static Set<Type> getTypesUsed(CFGraph cfg) {
		Set<Type> ret = Sets.newHashSet();

		for (CFNode node : cfg) {
			if (node instanceof StatementNode) {
				 Statement stmt = ((StatementNode) node)
						.getStatement();

				if (stmt instanceof VariableDeclarationStatement) {
					VariableDeclarationStatement vds = (VariableDeclarationStatement) stmt;
					ret.add(vds.getType());
				}
			}
		}

		return ret;
	}

}
