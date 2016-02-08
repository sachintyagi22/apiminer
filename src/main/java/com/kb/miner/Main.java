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
			List<List<ConcreteUse>> clusters = ConcreteUseClusterer
					.cluster(null);

			// abstract each cluster, then print it
			List<AbstractUse> abstractUses = new LinkedList<>();
			for (List<ConcreteUse> cluster : clusters) {
				AbstractUse use = AbstractUse.abstractUse(cluster);
				logger.debug("{}", use);
			}
	}
}
