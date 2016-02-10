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
 * Contains database and fragment handling.
 * <p>
 * The graphs that are searched for frequent substructures can be enhanced by a mining algorithm
 * to {@link com.kb.miner.miner.general.DataBaseGraph}s with spezialized abilities. These are also
 * used to recover the original {@link com.kb.miner.graph.Graph} objects.
 * <p>
 * The found frequent substructures are represented as {@link com.kb.miner.miner.general.Fragment}s.
 * Each instance of a fragment in the database is represented as an {@link com.kb.miner.miner.general.Embedding}.
 * There are object-orientated and high performace versions of fragments and embeddings available 
 * (for details see {@link de.parsemis.graph}).
 * <p>
 * UML-Diagram of the connection of {@link com.kb.miner.miner.general.DataBaseGraph},
 * {@link com.kb.miner.miner.general.Fragment}, and {@link com.kb.miner.miner.general.Embedding}:<br/>
 * <img src="doc-files/DB-Frag-Emb.png">
 * <p>
 * @author Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 */
package com.kb.miner.miner.general;
