/**
 * created 23.01.2008
 * 
 * @by Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 *
 * Copyright 2008 Marc Woerlein
 * 
 * This file is part of parsemis.
 *
 * Licence: 
 *  LGPL: http://www.gnu.org/licenses/lgpl.html
 *   EPL: http://www.eclipse.org/org/documents/epl-v10.php
 *   See the LICENSE file in the project's top-level directory for details.
 */

package com.kb.miner.algorithms.gaston;

import com.kb.miner.miner.environment.LocalEnvironment;
import com.kb.miner.miner.environment.ThreadEnvironment;
import com.kb.miner.miner.environment.ThreadEnvironmentFactory;
import com.kb.miner.utils.SynchronizedCounter;

public class GastonEnvironmentFactory<NodeType, EdgeType> implements
		ThreadEnvironmentFactory<NodeType, EdgeType> {

	final int nodeLabelCount, edgeLabelCount;
	final SynchronizedCounter counter;

	int maxNodeIndex;

	public GastonEnvironmentFactory(final int nodeLabelCount,
			final int edgeLabelCount, final SynchronizedCounter counter) {
		this.nodeLabelCount = nodeLabelCount;
		this.edgeLabelCount = edgeLabelCount;
		this.counter = counter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parsemis.miner.environment.ThreadEnvironmentFactory#getNewEnvironement(int,
	 *      de.parsemis.miner.environment.LocalEnvironment)
	 */
	public ThreadEnvironment<NodeType, EdgeType> getNewEnvironment(
			final int idx, final LocalEnvironment<NodeType, EdgeType> env) {
		return new GastonEnvironment<NodeType, EdgeType>(idx, edgeLabelCount,
				nodeLabelCount, maxNodeIndex, !env.findPathsOnly
						&& !env.findTreesOnly, env.storeHierarchicalEmbeddings,
				counter);
	}

}