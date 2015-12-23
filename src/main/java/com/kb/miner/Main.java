package com.kb.miner;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kb.java.dom.expression.UnknownExpressionException;
import com.kb.java.dom.naming.Type;
import com.kb.java.model.AbstractUse;
import com.kb.java.model.ConcreteUse;
import com.kb.java.model.ConcreteUseClusterer;
import com.kb.java.parse.ClassDeclaration;

/**
 * Main entry point.
 */
public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	@SuppressWarnings("unused")
	public static void main(String[] args) throws UnknownExpressionException {
		String rootPath = args[0];
		File rootFile = new File(rootPath);

		Miner miner = new Miner();
		miner.mine(rootFile);

		for (Type type : miner.getMinedTypes()) {
			if (!type.toString().equals("HttpRoute")) {
				logger.debug("Skipping type: {}", type);
				continue;
			}
			
			logger.debug("Analyzing uses of: {}" , type);

			Set<ClassDeclaration> decs = miner.getClassDecs(type);
			List<ConcreteUse> concreteUses = new LinkedList<>();

			for (ClassDeclaration dec : decs) {
				List<ConcreteUse> uses = ConcreteUse.extractUses(type, dec);
				/*
				 * for(ConcreteUse c: uses){ if(c.getCFG().asList().size() >0)
				 * concreteUses.add(c); }
				 */
				concreteUses.addAll(uses);

			}

			logger.debug("Analyzing uses of: {} concrete uses" , concreteUses.size());

			for (ConcreteUse use : concreteUses) {
				logger.debug("{}", use);
			}

			// if more than n, then cluster them
			List<List<ConcreteUse>> clusters = ConcreteUseClusterer
					.cluster(concreteUses);

			// abstract each cluster, then print it
			List<AbstractUse> abstractUses = new LinkedList<>();
			for (List<ConcreteUse> cluster : clusters) {
				AbstractUse use = AbstractUse.abstractUse(cluster);
				logger.debug("{}", use);
			}
			return;
		}

	}
}
