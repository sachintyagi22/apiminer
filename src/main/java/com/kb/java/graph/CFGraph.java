package com.kb.java.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.java.dom.condition.Condition;
import com.kb.java.dom.expression.ExpressionAdapter;
import com.kb.java.dom.expression.Expression;
import com.kb.java.dom.expression.UnknownExpressionException;
import com.kb.java.dom.naming.Declaration;
import com.kb.java.dom.statement.StatementAdapter;
import com.kb.java.dom.statement.ReturnStatement;
import com.kb.java.dom.statement.ThrowStatement;
import com.kb.java.parse.ClassVariableResolver;
import com.kb.utils.Tools;

/**
 * The control flow graph for a single method.
 * 
 * @author sachint
 *
 */
public class CFGraph implements Iterable<CFNode> {

	public static final Logger logger = LoggerFactory.getLogger(CFGraph.class);
	private CFNode entry;
	private MethodDeclaration methodSig;
	private Map<Statement, CFNode> stmtNodeMap = new HashMap<>();
	private ClassVariableResolver resolver;
	private ExpressionAdapter eclipseExpressionAdapter;
	private StatementAdapter eclipseStatementAdapter;
	private CompilationUnit unit;
	

	public CFGraph() {
	}
	
	public CFGraph(MethodDeclaration methodSig, ClassVariableResolver resolver, CompilationUnit unit)
			throws UnknownExpressionException {
		this.eclipseStatementAdapter = new StatementAdapter(resolver, unit);
		this.eclipseExpressionAdapter = new ExpressionAdapter(resolver);
		this.resolver = resolver;
		this.unit = unit;
		createCFG(methodSig);
	}

	public CFGraph(CFNode entry, MethodDeclaration methodSig) {
		this.entry = entry;
		this.methodSig = methodSig;
	}

	@SuppressWarnings("unchecked")
	private void createCFG(MethodDeclaration meth)
			throws UnknownExpressionException {
		methodSig = meth;
		Block body = meth.getBody();
		List<VariableDeclaration> params = meth.parameters();
		
		for (VariableDeclaration param : params) {
			int startPosition = param.getStartPosition();
			int line = unit.getLineNumber(startPosition)-1;
			int column = unit.getColumnNumber(startPosition);
			String shortType = param.getStructuralProperty(
					SingleVariableDeclaration.TYPE_PROPERTY).toString();
			String name = param.getName().toString();
			String fullType = resolver.getSafeType(shortType);
			resolver.addVarType(name, fullType, line, column);
		}

		if (body == null)
			return;

		List<Statement> statements = body.statements();
		entry = makeNode(statements, null, null, null);
	}

	/**
	 * Deal with control flow structures.
	 * 
	 * @param statements
	 * @param returnTo
	 * @param loopExit
	 * @param switchHead
	 * @return
	 * @throws UnknownExpressionException
	 */
	@SuppressWarnings("unchecked")
	private CFNode makeNode(List<Statement> statements, CFNode returnTo,
			CFNode loopExit, CFNode switchHead)
			throws UnknownExpressionException {

		if (statements.isEmpty()) {
			return returnTo;
		}

		Statement currentStmt = statements.get(0);

		// avoid duplicating
		CFNode result = stmtNodeMap.get(currentStmt);
		if (result != null)
			return result;

		List<Statement> rest;

		if (statements.size() == 1) {
			rest = new LinkedList<Statement>();
		} else {
			rest = statements.subList(1, statements.size());
		}

		if (currentStmt.getNodeType() == ASTNode.LABELED_STATEMENT) {
			org.eclipse.jdt.core.dom.LabeledStatement lbledstmt = (org.eclipse.jdt.core.dom.LabeledStatement) currentStmt;
			currentStmt = lbledstmt.getBody();
		}

		if (currentStmt.getNodeType() == ASTNode.IF_STATEMENT) {
			IfStatement ifstmt = (IfStatement) currentStmt;

			IfNode ifnode = new IfNode(
					eclipseExpressionAdapter.translateCondition(ifstmt
							.getExpression()));
			CFNode thenBlockNode = makeNode(
					Tools.makeList(ifstmt.getThenStatement()), null, loopExit,
					switchHead);
			ThenBranchNode thenNode = new ThenBranchNode();
			thenNode.setBranchHead(ifnode);
			thenNode.setNext(thenBlockNode);

			if (ifstmt.getElseStatement() != null) {
				ElseBranchNode elseNode = new ElseBranchNode();
				elseNode.setBranchHead(ifnode);
				CFNode elseBlockNode = makeNode(
						Tools.makeList(ifstmt.getElseStatement()), null,
						loopExit, switchHead);
				elseNode.setNext(elseBlockNode);
				ifnode.setElseNode(elseNode);
			}

			CFNode afterNode = makeNode(rest, returnTo, loopExit, switchHead);

			ifnode.setExitNode(afterNode);
			ifnode.setThenNode(thenNode);

			stmtNodeMap.put(currentStmt, ifnode);
			return ifnode;
		}

		else if (currentStmt.getNodeType() == ASTNode.SWITCH_STATEMENT) {
			SwitchStatement switchStmt = (SwitchStatement) currentStmt;

			SwitchNode snode = new SwitchNode(
					eclipseExpressionAdapter.translate(switchStmt
							.getExpression()));

			CFNode afterNode = makeNode(rest, returnTo, loopExit, switchHead);

			List<Statement> stmts = switchStmt.statements();

			makeNode(stmts, afterNode, afterNode, snode);

			stmtNodeMap.put(currentStmt, snode);
			return snode;
		}

		else if (currentStmt.getNodeType() == ASTNode.SWITCH_CASE) {
			SwitchCase switchCase = (SwitchCase) currentStmt;

			SwtichCaseNode cnode = new SwtichCaseNode(
					eclipseExpressionAdapter.translate(switchCase
							.getExpression()));

			SwitchNode parentNode = (SwitchNode) switchHead;

			SwitchToCaseNode stcn = new SwitchToCaseNode(parentNode, cnode);

			parentNode.addCase(stcn);

			CFNode next = makeNode(rest, returnTo, loopExit, switchHead);
			cnode.setNext(next);

			stmtNodeMap.put(currentStmt, cnode);
			return cnode;
		}

		else if (currentStmt.getNodeType() == ASTNode.WHILE_STATEMENT) {
			WhileStatement whilestmt = (WhileStatement) currentStmt;

			WhileNode whilenode = new WhileNode(
					eclipseExpressionAdapter.translateCondition(whilestmt
							.getExpression()));

			CFNode afterNode = makeNode(rest, returnTo, loopExit, switchHead);

			ExitLoopNode exitNode = new ExitLoopNode();
			exitNode.setLoopHead(whilenode);
			exitNode.setNext(afterNode);

			whilenode.setExitNode(exitNode);
			whilenode.setBodyNode(makeNode(Tools.makeList(whilestmt.getBody()),
					exitNode, exitNode, switchHead));

			stmtNodeMap.put(currentStmt, whilenode);
			return whilenode;
		}

		/**
		 * For our pruposes do and while can be treated the same
		 */
		else if (currentStmt.getNodeType() == ASTNode.DO_STATEMENT) {
			DoStatement dostmt = (DoStatement) currentStmt;

			WhileNode whilenode = new WhileNode(
					eclipseExpressionAdapter.translateCondition(dostmt
							.getExpression()));

			CFNode afterNode = makeNode(rest, returnTo, loopExit, switchHead);

			ExitLoopNode exitNode = new ExitLoopNode();
			exitNode.setLoopHead(whilenode);
			exitNode.setNext(afterNode);

			whilenode.setExitNode(exitNode);
			whilenode.setBodyNode(makeNode(Tools.makeList(dostmt.getBody()),
					exitNode, exitNode, switchHead));

			stmtNodeMap.put(currentStmt, whilenode);
			return whilenode;
		}

		else if (currentStmt.getNodeType() == ASTNode.ENHANCED_FOR_STATEMENT) {
			org.eclipse.jdt.core.dom.EnhancedForStatement fstmt = (org.eclipse.jdt.core.dom.EnhancedForStatement) currentStmt;

			Expression exp = eclipseExpressionAdapter.translate(fstmt
					.getExpression());
			Declaration declar = ExpressionAdapter
					.translateDeclaration(fstmt.getParameter());

			EnhancedForNode forNode = new EnhancedForNode(declar, exp);

			CFNode afterNode = makeNode(rest, returnTo, loopExit, switchHead);

			ExitLoopNode exitNode = new ExitLoopNode();
			exitNode.setLoopHead(forNode);
			exitNode.setNext(afterNode);

			forNode.setExitNode(exitNode);
			forNode.setBodyNode(makeNode(Tools.makeList(fstmt.getBody()),
					exitNode, exitNode, switchHead));

			stmtNodeMap.put(currentStmt, forNode);
			return forNode;

		}

		else if (currentStmt.getNodeType() == ASTNode.FOR_STATEMENT) {
			org.eclipse.jdt.core.dom.ForStatement fstmt = (org.eclipse.jdt.core.dom.ForStatement) currentStmt;

			Condition c = eclipseExpressionAdapter.translateCondition(fstmt
					.getExpression());

			// TODO: ignoring these for now
			// fstmt.initializers();
			// fstmt.updaters();

			ForNode forNode = new ForNode(c);

			CFNode afterNode = makeNode(rest, returnTo, loopExit, switchHead);

			ExitLoopNode exitNode = new ExitLoopNode();
			exitNode.setLoopHead(forNode);
			exitNode.setNext(afterNode);

			forNode.setExitNode(exitNode);
			forNode.setBodyNode(makeNode(Tools.makeList(fstmt.getBody()),
					exitNode, loopExit, switchHead));

			stmtNodeMap.put(currentStmt, forNode);
			return forNode;

		}

		// need to exit current if block or loop block
		else if (currentStmt.getNodeType() == ASTNode.BREAK_STATEMENT
				|| currentStmt.getNodeType() == ASTNode.CONTINUE_STATEMENT) {
			BreakNode breaknode = new BreakNode(loopExit);

			makeNode(rest, returnTo, loopExit, switchHead);

			stmtNodeMap.put(currentStmt, breaknode);
			return breaknode;
		} else if (currentStmt.getNodeType() == ASTNode.TRY_STATEMENT) {
			TryStatement trystmt = (TryStatement) currentStmt;

			List<Statement> trystatements = new LinkedList<Statement>();

			trystatements.addAll(trystmt.getBody().statements());

			if (trystmt.getFinally() != null) {
				trystatements.addAll(trystmt.getFinally().statements());
			}

			CFNode afterNode = makeNode(rest, returnTo, loopExit, switchHead);
			CFNode trybody = makeNode(trystatements, afterNode, loopExit,
					switchHead);

			stmtNodeMap.put(currentStmt, trybody);
			return trybody;
		}

		else if (currentStmt.getNodeType() == ASTNode.BLOCK) {
			Block block = (Block) currentStmt;
			CFNode blockNode = makeNode(block.statements(), returnTo, loopExit,
					switchHead);

			stmtNodeMap.put(currentStmt, blockNode);
			return blockNode;
		}

		// some other statement type

		List<com.kb.java.dom.statement.Statement> stmts = eclipseStatementAdapter
				.translateSafe(currentStmt);

		int charIndex = currentStmt.getStartPosition();

		// System.out.println("Start Position for " + currentStmt + " = " +
		// charIndex);

		if (stmts.isEmpty())
			return makeNode(rest, returnTo, loopExit, switchHead);

		StatementNode prevNode = null;
		StatementNode stmtnode = null;

		for (com.kb.java.dom.statement.Statement stmt : stmts) {
			/**
			 * need something like
			 * 
			 * if stmt contains conditional expression, de sugar: add if stmt
			 * and each possibility below it
			 */

			stmt.setCharIndex(charIndex);
			stmtnode = new StatementNode(stmt);

			if (prevNode != null) {
				prevNode.setNext(stmtnode);
			}

			prevNode = stmtnode;
		}

		if (stmtnode.getStatement() instanceof ReturnStatement
				|| stmtnode.getStatement() instanceof ThrowStatement) {
			stmtnode.setNext(null);
		} else {
			stmtnode.setNext(makeNode(rest, returnTo, loopExit, switchHead));
		}

		stmtNodeMap.put(currentStmt, stmtnode);
		return stmtnode;
	}

	public CFNode getEntry() {
		return entry;
	}

	@Override
	public String toString() {
		return entry.toString();//buf.toString();
	}

	public MethodDeclaration getMethodSig() {
		return methodSig;
	}

	public void setEntry(CFNode entry) {
		this.entry = entry;
	}

	public boolean isEmpty() {
		return entry == null;
	}

	public List<CFNode> asList() {
		Set<CFNode> unvisited = new HashSet<CFNode>();
		List<CFNode> visited = new ArrayList<CFNode>();

		unvisited.add(getEntry());

		while (!unvisited.isEmpty()) {
			CFNode n = unvisited.iterator().next();

			if (n == null) {
				unvisited.remove(n);
				continue;
			}

			unvisited.remove(n);
			visited.add(n);

			/*for (CFNode node : n.getSuccessors()) {
				if (node != null && !visited.contains(node)) {
					unvisited.add(node);
				}
			}*/
			
			CFNode node = n.getNext();
			if (node != null && !visited.contains(node)) {
				unvisited.add(node);
			}

		}
		return visited;
	}

	@Override
	public Iterator<CFNode> iterator() {
		return asList().iterator();
	}

}