/**
 * created Aug 20, 2008
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

/**
 * Contains different strategies for the traversal of a search lattice.
 * <p>
 * Memorize {@link com.kb.miner.miner.chain.SearchLatticeNode} objects as well
 * as multi-threaded distribution is handled in this package.
 * <p>
 * The required initial nodes and how to extend them (see the 
 * {@link com.kb.miner.miner.chain.Extender} class) depends on the
 * {@link com.kb.miner.algorithms.Algorithm}.
 * <p>
 * @author Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 */
package com.kb.miner.strategy;
