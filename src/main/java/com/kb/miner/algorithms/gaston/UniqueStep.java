/**
 * created Jan 10, 2008
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

import com.kb.miner.graph.HPGraph;
import com.kb.miner.miner.chain.Extension;
import com.kb.miner.miner.chain.MiningStep;
import com.kb.miner.miner.chain.SearchLatticeNode;
import com.kb.miner.miner.environment.LocalEnvironment;
import com.kb.miner.miner.environment.Statistics;

import java.util.Collection;

/**
 * @author Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 * 
 * @param <NodeType>
 *            the type of the node labels (will be hashed and checked with
 *            .equals(..))
 * @param <EdgeType>
 *            the type of the edge labels (will be hashed and checked with
 *            .equals(..))
 */
public class UniqueStep<NodeType, EdgeType> extends
		MiningStep<NodeType, EdgeType> {

	final private Collection<HPGraph<NodeType, EdgeType>> found;

	final private Statistics stats;

	public UniqueStep(final MiningStep<NodeType, EdgeType> next,
			final Collection<HPGraph<NodeType, EdgeType>> unique,
			final Statistics stats) {
		super(next);
		found = unique;
		this.stats = stats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.parsemis.miner.chain.MiningStep#call(de.parsemis.miner.general.SearchLatticeNode,
	 *      java.util.Collection)
	 */
	@Override
	public void call(final SearchLatticeNode<NodeType, EdgeType> node,
			final Collection<Extension<NodeType, EdgeType>> extensions) {
		if (node instanceof GastonCycle) {
			assert node.toHPFragment().toHPGraph() != null : node;
			final long before = System.currentTimeMillis();
			synchronized (found) {
				stats.syncTime[3] += System.currentTimeMillis() - before;
				if (!found.add(node.toHPFragment().toHPGraph())) {
					node.store(false);
					LocalEnvironment.env(this).stats.duplicateFragments++;
					// duplicated graphs need not to be extended
					return;
				}
			}
		}
		this.callNext(node, extensions);
	}

}
