package com.kb.java.dom.statement;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.java.dom.expression.ExpressionAdapter;
import com.kb.java.dom.expression.Expression;
import com.kb.java.dom.expression.UnknownExpressionException;
import com.kb.java.dom.expression.Variable;
import com.kb.java.dom.naming.Type;
import com.kb.java.parse.ClassVariableResolver;

public class StatementAdapter {

	public static final Logger logger = LoggerFactory
			.getLogger(StatementAdapter.class);
	
	private ExpressionAdapter eclipseExpressionAdapter;
	private ClassVariableResolver resolver;
	private CompilationUnit unit;
	
	public StatementAdapter(ClassVariableResolver resolver, CompilationUnit unit) {
		this.eclipseExpressionAdapter = new ExpressionAdapter(resolver, unit);
		this.resolver = resolver;
		this.unit = unit;
	}

	public List<Statement> translateSafe(
			org.eclipse.jdt.core.dom.Statement stmt) {
		List<Statement> ret;
		try {
			ret = translate(stmt);
			return ret;
		} catch (UnknownExpressionException e) {
			logger.warn(" IGNORING expression : {}", e.toString());
			return new LinkedList<Statement>();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Statement> translate(
			org.eclipse.jdt.core.dom.Statement stmt)
			throws UnknownExpressionException {

		// System.out.println("Translating stmt: " + stmt + " -- " +
		// stmt.getClass());

		List<Statement> stmtList = new LinkedList<Statement>();

		if (stmt instanceof org.eclipse.jdt.core.dom.ReturnStatement) {
			org.eclipse.jdt.core.dom.ReturnStatement retstmt = (org.eclipse.jdt.core.dom.ReturnStatement) stmt;

			ReturnStatement rt = new ReturnStatement(
					eclipseExpressionAdapter.translate(retstmt.getExpression()));

			stmtList.add(rt);
		} else if (stmt instanceof org.eclipse.jdt.core.dom.SuperConstructorInvocation) {
			@SuppressWarnings("unused")
			org.eclipse.jdt.core.dom.SuperConstructorInvocation sci = (org.eclipse.jdt.core.dom.SuperConstructorInvocation) stmt;

			// TODO: handle super constructor invocations
		} else if (stmt instanceof org.eclipse.jdt.core.dom.SynchronizedStatement) {
			org.eclipse.jdt.core.dom.SynchronizedStatement sync = (org.eclipse.jdt.core.dom.SynchronizedStatement) stmt;

			List<org.eclipse.jdt.core.dom.Statement> body = sync.getBody()
					.statements();

			for (org.eclipse.jdt.core.dom.Statement s : body) {
				stmtList.addAll(translate(s));
			}
		} else if (stmt instanceof org.eclipse.jdt.core.dom.ThrowStatement) {
			org.eclipse.jdt.core.dom.ThrowStatement retstmt = (org.eclipse.jdt.core.dom.ThrowStatement) stmt;

			ThrowStatement rt = new ThrowStatement(
					eclipseExpressionAdapter.translate(retstmt.getExpression()));

			stmtList.add(rt);
		} else if (stmt instanceof org.eclipse.jdt.core.dom.ExpressionStatement) {

			Expression e = eclipseExpressionAdapter
					.translate(((org.eclipse.jdt.core.dom.ExpressionStatement) stmt)
							.getExpression());

			ExpressionStatement es = new ExpressionStatement(e);

			stmtList.add(es);
		} else if (stmt instanceof org.eclipse.jdt.core.dom.VariableDeclarationStatement) {
			org.eclipse.jdt.core.dom.VariableDeclarationStatement vdstmt = (org.eclipse.jdt.core.dom.VariableDeclarationStatement) stmt;

			org.eclipse.jdt.core.dom.Type t = vdstmt.getType();
			Type type = ExpressionAdapter
					.translateType(t);
			int line = unit.getLineNumber(t.getStartPosition());
			int col = unit.getColumnNumber(t.getStartPosition());
			String fullType = resolver.getSafeType(type.toString());
			type = new Type(fullType);
			List<VariableDeclarationFragment> frags = vdstmt.fragments();

			for (VariableDeclarationFragment frag : frags) {
				
				String varName = frag.getName().toString();
				Variable var = new Variable(varName);

				VariableDeclarationStatement ret;

				if (frag.getInitializer() == null) {
					ret = new VariableDeclarationStatement(type, var);
				} else {
					ret = new VariableDeclarationStatement(type, var,
							eclipseExpressionAdapter.translate(frag
									.getInitializer()));
				}
				resolver.addVarType(varName, fullType, line, col, t.getLength());

				stmtList.add(ret);
			}
		} else if (stmt instanceof org.eclipse.jdt.core.dom.LabeledStatement) {
			org.eclipse.jdt.core.dom.LabeledStatement lbledstmt = (org.eclipse.jdt.core.dom.LabeledStatement) stmt;

			stmtList.addAll(translate(lbledstmt.getBody()));
		} else if (stmt instanceof org.eclipse.jdt.core.dom.EmptyStatement) {
			// ignore these
		} else
			throw new UnknownExpressionException(stmt);

		return stmtList;

	}

}
