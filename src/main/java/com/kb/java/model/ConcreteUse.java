package com.kb.java.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.kb.java.dom.expression.Variable;
import com.kb.java.dom.naming.Declaration;
import com.kb.java.dom.naming.MethodSignature;
import com.kb.java.dom.naming.Type;
import com.kb.java.dom.statement.Statement;
import com.kb.java.dom.statement.VariableDeclarationStatement;
import com.kb.java.graph.CFGraph;
import com.kb.java.graph.CFNode;
import com.kb.java.graph.StatementNode;
import com.kb.java.graph.tools.CFGFilterer;
import com.kb.java.graph.tools.SingleInvocationPerStatementTransformer;
import com.kb.java.parse.ClassDeclaration;

public class ConcreteUse {

	private Declaration seed;
	private List<CFGraph> cfgs = Lists.newArrayList();
	private Set<Declaration> auxVars = Sets.newHashSet();
	private ClassDeclaration dec;

	public static final Predicate<Object> NOTNULL = new Predicate<Object>() {
		public boolean apply(Object input) {
			return input != null;
		}
	};

	public ConcreteUse(Declaration seed) {
		this.seed = seed;
	}

	public static List<ConcreteUse> extractUses(final Type type,
			ClassDeclaration classDec) {

		List<ConcreteUse> ret = Lists.newArrayList();

		// Get a list of use seeds
		// TODO: use ITypeBindings if they exist to get true types
		Set<Declaration> auxVarToType = getDeclaredVariables(classDec);

		// filter for use seeds (declarations matching type)
		Set<Declaration> seeds = Sets.filter(auxVarToType,
				new Predicate<Declaration>() {
					public boolean apply(Declaration input) {
						return input.getType().equals(type);
					}
				});

		if (seeds.isEmpty())
			return Collections.emptyList();

		// For each seed, Collect all the information that goes with each use
		for (Declaration seed : seeds) {
			ConcreteUse use = new ConcreteUse(seed);
			use.addAuxVariables(auxVarToType);
			ret.add(use);
			use.dec = classDec;

			// TODO: add ast for field inits

			for (MethodSignature sig : classDec.getMethods()) {
				CFGraph cfg = sig.getCFG();
				String origCFG = cfg.toString();
				final Variable thisSeed = seed.getVariable();
				
				Predicate<CFNode> relevantNodes = new Predicate<CFNode>() {
					public boolean apply(CFNode node) {
						if (node instanceof StatementNode) {
							Statement statement = ((StatementNode) node)
									.getStatement();

							if (statement instanceof VariableDeclarationStatement) {
								if (((VariableDeclarationStatement) statement)
										.getVariable().equals(thisSeed))
									return true;
							}

							return statement.getAllSubExpressions().contains(
									thisSeed);
						}
						return false;
					}
				};
				
				// filter each method cfg
				// only statements relevant to var (
				CFGFilterer cfgFilterer = new CFGFilterer(relevantNodes);
				CFGraph seededCFG = cfgFilterer.apply(cfg);

				// transform cfg so that each statement contains only one method
				// call
				SingleInvocationPerStatementTransformer singleInvokeTransformer = new SingleInvocationPerStatementTransformer();
				singleInvokeTransformer.apply(seededCFG);
				
				

				if (!seededCFG.isEmpty()) {
					use.addCFG(seededCFG);
					System.out.println("Orig CFG:" + cfg.getMethodSig() + " \n " + origCFG);
					/*System.out.println("\n Seed:  " + seed);
					System.out.println("\n\n Seeded CFG:  " + seededCFG);
					System.out.println("\n\n\n\n\n");*/
				}
			}
		}
		return ret;
	}

	private static Set<Declaration> getDeclaredVariables(
			ClassDeclaration classDec) {
		Set<Declaration> ret = Sets.newHashSet();

		// Fields
		for (VariableDeclarationStatement field : classDec.getFields()) {
			ret.add(new Declaration(field.getType(), field.getVariable()));
		}

		// parameters and stuff in methods
		for (MethodSignature method : classDec.getMethods()) {
			ret.addAll(method.getParameters());

			Iterable<Declaration> decs = Iterables.filter(Iterables.transform(
					method.getCFG(), new Function<CFNode, Declaration>() {
						public Declaration apply(CFNode node) {
							if (node instanceof StatementNode) {
								Statement statement = ((StatementNode) node)
										.getStatement();
								// Assuming decs cant be nested inside
								// some other stmt
								if (statement instanceof VariableDeclarationStatement) {
									VariableDeclarationStatement vds = ((VariableDeclarationStatement) statement);
									return new Declaration(vds.getType(), vds
											.getVariable());
								}
							}
							return null;
						}
					}), NOTNULL);

			ret.addAll(Lists.newArrayList(decs));
		}
		return ret;
	}

	private void addCFG(CFGraph cfg) {
		cfgs.add(cfg);
	}

	private void addAuxVariables(Set<Declaration> auxVarToType) {
		auxVars.addAll(auxVarToType);
	}
	
	public CFGraph getCFG(){
		if(cfgs.size() > 0){
			return cfgs.get(0);
		}
		return new CFGraph();
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Concrete Use of: " + seed + "in class : " + dec.getType()
				+ "\n");
		//int i = 0;
		for (CFGraph cfg : cfgs) {
			//buf.append("\n \n #################### " + i++ + " \n");
			buf.append(cfg.toString());
		}
		return buf.toString();
	}
}
