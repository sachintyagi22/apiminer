package com.kb.java.graph.tools;

import java.util.List;

import com.google.common.base.Predicate;
import com.kb.java.dom.expression.UnknownExpressionException;
import com.kb.java.dom.expression.Variable;
import com.kb.java.dom.naming.MethodSignature;
import com.kb.java.dom.statement.Statement;
import com.kb.java.graph.CFGraph;
import com.kb.java.graph.CFNode;
import com.kb.java.graph.ElseBranchNode;
import com.kb.java.graph.EnhancedForNode;
import com.kb.java.graph.ExitLoopNode;
import com.kb.java.graph.ForNode;
import com.kb.java.graph.IfNode;
import com.kb.java.graph.StatementNode;
import com.kb.java.graph.SwitchNode;
import com.kb.java.graph.ThenBranchNode;
import com.kb.java.graph.WhileNode;
import com.kb.java.parse.ClassDeclaration;
import com.kb.java.parse.CFGParser;

public class CFGFilterer {

	Predicate<CFNode> nodesToKeep;

	public CFGFilterer(Predicate<CFNode> nodesToKeep) {
		this.nodesToKeep = nodesToKeep;
	}

	public CFGraph apply(CFGraph inputCFG) {
		return new CFGraph(processNode(inputCFG.getEntry()), inputCFG.getMethodSig());
	}

	@SuppressWarnings("unused")
	CFNode processNode(CFNode node) {

		// cache results

		if (node == null)
			return null;

		if (node instanceof StatementNode) {
			StatementNode sn = (StatementNode) node;
			if (nodesToKeep.apply(sn)) {
				sn.setNext(processNode(sn.getSuccessor()));
				return sn;
			} else
				return processNode(sn.getSuccessor());
		} else if (node instanceof WhileNode) {
			WhileNode wn = (WhileNode) node;
			CFNode processedBody = processNode(wn.getBodyNode());
			CFNode processedSucessor = processNode(wn.getExitNode());
			if (processedBody instanceof ExitLoopNode && processedBody == processedSucessor)
				return ((ExitLoopNode) processedBody).getNext();
			wn.setBodyNode(processedBody);
			wn.setExitNode(processedSucessor);
			return wn;
		} else if (node instanceof ForNode) {
			ForNode fn = (ForNode) node;
			CFNode processedBody = processNode(fn.getBodyNode());
			CFNode processedSucessor = processNode(fn.getExitNode());
			if (processedBody instanceof ExitLoopNode && processedBody == processedSucessor)
				return ((ExitLoopNode) processedBody).getNext();
			fn.setBodyNode(processedBody);
			fn.setExitNode(processedSucessor);
			return fn;
		} else if (node instanceof EnhancedForNode) {
			EnhancedForNode fn = (EnhancedForNode) node;
			CFNode processedBody = processNode(fn.getBodyNode());
			CFNode processedSucessor = processNode(fn.getExitNode());
			if (processedBody instanceof ExitLoopNode && processedBody == processedSucessor)
				return ((ExitLoopNode) processedBody).getNext();
			fn.setBodyNode(processedBody);
			fn.setExitNode(processedSucessor);
			return fn;
		}
		else if (node instanceof IfNode) {
			IfNode in = (IfNode) node;
			ThenBranchNode processedThen = (ThenBranchNode) processNode(in.getThenNode());
			ElseBranchNode processedElse = (ElseBranchNode) processNode(in.getElseNode());
			CFNode processedExit = processNode(in.getExitNode());
			if (processedThen == null || processedThen.getNext() == null)
				return processedExit;

			try {
				if (processedThen.getNext().equals(processedExit)
						&& (processedElse == null || processedElse.getNext() == null || processedElse.getNext().equals(processedExit)))
					return processedExit;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in.setThenNode(processedThen);
			in.setElseNode(processedElse);
			in.setExitNode(processedExit);
			return in;
		}
		else if (node instanceof ThenBranchNode) {
			ThenBranchNode tbn = (ThenBranchNode) node;
			tbn.setNext(processNode(tbn.getNext()));
			return tbn;
		}
		else if (node instanceof ElseBranchNode) {
			ElseBranchNode ebn = (ElseBranchNode) node;
			ebn.setNext(processNode(ebn.getNext()));
			return ebn;
		} else if (node instanceof ExitLoopNode) {
			ExitLoopNode eln = (ExitLoopNode) node;
			eln.setNext(processNode(eln.getNext()));
			return eln;
		}else if (node instanceof SwitchNode) {
			SwitchNode eln = (SwitchNode) node;
			// Ignore switch eln.setNext(processNode(eln.));
			return null;
		}
		return null;
		//throw new IllegalStateException("Unknown node type: " + node + " " + node.getClass());
	}

	public static void main(String[] args) throws UnknownExpressionException {

		String testClassSrc =
				" public class Test { " +
						"   void fun() { " +
						"        Var theVar = new Var();          " +
						"      Object a = foo(); " +
						"       b = foo().bar(); " +
						"	   Object c = theVar.baz(); " +
						"      while (x > y) { " +
						"	     if( yada() ) { " +
						"         c = frm.bat(); " +
						"        } else { }" +
						"      }  " +
						"       while(yada.hasNext()) " +
						"         d = barg(theVar);  " +
						"    } " +
						"  } ";

		CFGParser parser = new CFGParser();
		List<ClassDeclaration> classes = parser.parse(testClassSrc);
		ClassDeclaration testClass = classes.get(0);
		MethodSignature method = testClass.getMethods().get(0);
		CFGraph fun = method.getCFG();
		System.out.println(fun);
		System.out.println("--------");

		final Variable thisSeed = new Variable("theVar");
		Predicate<CFNode> relevantNodes = new Predicate<CFNode>() {
			public boolean apply(CFNode node) {
				if (node instanceof StatementNode) {
					Statement statement = ((StatementNode) node).getStatement();
					return statement.getAllSubExpressions().contains(thisSeed);
				}
				return false;
			}
		};

		// filter each method cfg
		// only statements relevant to var (
		CFGFilterer filter = new CFGFilterer(relevantNodes);
		filter.apply(fun);
		System.out.println(fun);
	}
}
