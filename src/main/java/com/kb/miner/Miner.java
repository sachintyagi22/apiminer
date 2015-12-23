package com.kb.miner;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.java.dom.expression.UnknownExpressionException;
import com.kb.java.dom.naming.Type;
import com.kb.java.parse.ClassDeclaration;
import com.kb.java.parse.CFGParser;
import com.kb.utils.FileUtils;

public class Miner implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(Miner.class);
	
	private Map<Type, Set<ClassDeclaration>> typeToClassMap = new HashMap<>();
	private CFGParser parser = new CFGParser();
	private FileUtils fileUtils = new FileUtils();

	public void mine(File rootFile) throws UnknownExpressionException {
		
		List<File> sourceFiles = fileUtils.findAll(rootFile, "java");
		logger.debug("{} java source files found in {}", sourceFiles.size(), rootFile.getAbsolutePath());

		for (File srcFile : sourceFiles) {
			String source = fileUtils.readFile(srcFile);
			List<ClassDeclaration> classes = parser.parse(source);

			for (ClassDeclaration dec : classes) {
				Set<Type> usedTypes = Util.getTypesUsed(dec);

				for (Type type : usedTypes) {
					Set<ClassDeclaration> decs = typeToClassMap.get(type);

					if (decs == null) {
						decs = new HashSet<ClassDeclaration>();
						typeToClassMap.put(type, decs);
					}

					decs.add(dec);
				}
			}
		}

		logger.debug("Type \t NumberOfClasses");
		for (Type key : typeToClassMap.keySet()) {
			logger.debug(key + " \t " + typeToClassMap.get(key).size());
		}
	}

	public Set<ClassDeclaration> getClassDecs(Type type) {
		return typeToClassMap.get(type);
	}

	public Collection<Type> getMinedTypes() {
		return typeToClassMap.keySet();
	}
}
