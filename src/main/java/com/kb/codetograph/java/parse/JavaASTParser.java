package com.kb.codetograph.java.parse;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.kb.codetograph.utils.FileUtils;

public class JavaASTParser {

	private final boolean useBindings;
	private final boolean useJavadocs;
	private FileUtils fileUtils = new FileUtils();
	public enum ParseType {
		COMPILATION_UNIT, CLASS_BODY, METHOD, STATEMENTS, EXPRESSION
	}

	/**
	 * Constructor.
	 *
	 * @param useBindings
	 *            calculate bindings on the extracted AST.
	 */
	public JavaASTParser(final boolean useBindings) {
		this.useBindings = useBindings;
		useJavadocs = false;
	}

	public JavaASTParser(final boolean useBindings, final boolean useJavadocs) {
		this.useBindings = useBindings;
		this.useJavadocs = useJavadocs;
	}

	/**
	 * Get the AST of a file. It is assumed that a CompilationUnit will be
	 * returned. A heuristic is used to set the file's path variable.
	 *
	 * @param file
	 * @return the compilation unit of the file
	 * @throws IOException
	 */
	public final CompilationUnit getAST(final File file) throws IOException {
		return getAST(file, new HashSet<String>());
	}

	/**
	 * Get the AST of a file, including additional source paths to resolve
	 * cross-file bindings. It is assumed that a CompilationUnit will be
	 * returned. A heuristic is used to set the file's path variable.
	 * <p>
	 * Note: this may only yield a big improvement if the above heuristic fails
	 * and srcPaths contains the correct source path.
	 *
	 * @param file
	 * @param srcPaths
	 *            for binding resolution
	 * @return the compilation unit of the file
	 * @throws IOException
	 */
	public final CompilationUnit getAST(final File file,
			final Set<String> srcPaths) throws IOException {
		final String sourceFile = fileUtils.readFile(file);
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final Map<String, String> options = new Hashtable<String, String>();
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		if (useJavadocs) {
			options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
		}
		parser.setCompilerOptions(options);
		parser.setSource(sourceFile.toCharArray()); // set source
		parser.setResolveBindings(useBindings);
		parser.setBindingsRecovery(useBindings);

		parser.setStatementsRecovery(true);

		parser.setUnitName(file.getAbsolutePath());

		// Heuristic to retrieve source file path
		final String srcFilePath;
		if (file.getAbsolutePath().contains("/src")) {
			srcFilePath = file.getAbsolutePath().substring(0,
					file.getAbsolutePath().indexOf("src", 0) + 3);
		} else {
			srcFilePath = "";
		}

		// Add file to source paths if not already present
		srcPaths.add(srcFilePath);

		final String[] sourcePathEntries = srcPaths.toArray(new String[srcPaths
				.size()]);
		final String[] classPathEntries = new String[0];
		parser.setEnvironment(classPathEntries, sourcePathEntries, null, true);

		final CompilationUnit compilationUnit = (CompilationUnit) parser
				.createAST(null);
		return compilationUnit;
	}

	/**
	 * Get a compilation unit of the given file content.
	 *
	 * @param fileContent
	 * @param parseType
	 * @return the compilation unit
	 */
	public final ASTNode getAST(final String fileContent,
			final ParseType parseType) {
		return (ASTNode) getASTNode(fileContent, parseType);
	}

	/**
	 * Return an ASTNode given the content
	 *
	 * @param content
	 * @return
	 */
	public final ASTNode getASTNode(final char[] content,
			final ParseType parseType) {
		final ASTParser parser = ASTParser.newParser(AST.JLS8);
		final int astKind;
		switch (parseType) {
		case CLASS_BODY:
		case METHOD:
			astKind = ASTParser.K_CLASS_BODY_DECLARATIONS;
			break;
		case COMPILATION_UNIT:
			astKind = ASTParser.K_COMPILATION_UNIT;
			break;
		case EXPRESSION:
			astKind = ASTParser.K_EXPRESSION;
			break;
		case STATEMENTS:
			astKind = ASTParser.K_STATEMENTS;
			break;
		default:
			astKind = ASTParser.K_COMPILATION_UNIT;
		}
		parser.setKind(astKind);

		final Map<String, String> options = new Hashtable<String, String>();
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		if (useJavadocs) {
			options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
		}
		parser.setCompilerOptions(options);
		parser.setSource(content);
		parser.setResolveBindings(useBindings);
		parser.setBindingsRecovery(useBindings);

		parser.setStatementsRecovery(true);

		if (parseType != ParseType.METHOD) {
			return parser.createAST(null);
		} else {
			final ASTNode cu = parser.createAST(null);
			return getFirstMethodDeclaration(cu);
		}
	}

	/**
	 * Get the AST of a string. Path variables cannot be set.
	 *
	 * @param file
	 * @param parseType
	 * @return an AST node for the given file content
	 * @throws IOException
	 */
	public final ASTNode getASTNode(final String fileContent,
			final ParseType parseType) {
		return getASTNode(fileContent.toCharArray(), parseType);
	}
	
	private final MethodDeclaration getFirstMethodDeclaration(final ASTNode node) {
		final TopMethodRetriever visitor = new TopMethodRetriever();
		node.accept(visitor);
		return visitor.topDcl;
	}
	
	private static final class TopMethodRetriever extends ASTVisitor {
		public MethodDeclaration topDcl;

		@Override
		public boolean visit(final MethodDeclaration node) {
			topDcl = node;
			return false;
		}
	}

}
