/**
 * created Nov 13, 2007
 * 
 * @by Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 *
 * Copyright 2007 Marc Woerlein
 * 
 * This file is part of parsemis.
 *
 * Licence: 
 *  LGPL: http://www.gnu.org/licenses/lgpl.html
 *   EPL: http://www.eclipse.org/org/documents/epl-v10.php
 *   See the LICENSE file in the project's top-level directory for details.
 */
package com.kb.miner.jp;

import com.kb.miner.algorithms.Algorithm;
import com.kb.miner.miner.environment.LocalEnvironment;
import com.kb.miner.miner.general.Fragment;
import com.kb.miner.strategy.LocalStack;
import com.kb.miner.strategy.StackList;
import com.kb.miner.strategy.Worker;
import com.kb.miner.utils.Generic;

import java.util.Collection;

/**
 * This class represents a thread for the distributed JavaParty DFS search.
 * 
 * @author Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 * 
 * @param <NodeType>
 *            the type of the node labels (will be hashed and checked with
 *            .equals(..))
 * @param <EdgeType>
 *            the type of the edge labels (will be hashed and checked with
 *            .equals(..))
 * 
 * @remote
 */
public class RemoteJPThread<NodeType, EdgeType> extends Thread implements
		Generic<NodeType, EdgeType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int index;

	/**
	 * creates a new thread
	 * 
	 * @param idx
	 *            the index of the new thread
	 * @param algo
	 *            the algorithm that will be used
	 * @param list
	 *            the list of all stacks
	 * @param answer
	 *            the collection the found fragments will be stored in
	 */
	public RemoteJPThread(final int idx,
			final Algorithm<NodeType, EdgeType> algo,
			final StackList<NodeType, EdgeType> list,
			final Collection<Fragment<NodeType, EdgeType>> answer) {
		super(new Worker<NodeType, EdgeType>(
				new LocalStack<NodeType, EdgeType>(idx, list, LocalEnvironment
						.env(algo)), answer, algo.getExtender(idx), idx));
		index = idx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JPThread-" + index + " " + Host.name();
	}
}
