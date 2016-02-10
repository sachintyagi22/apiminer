/**
 * created Oct 09, 2007
 * 
 * @by Sebastian Lenz (siselenz@stud.informatik.uni-erlangen.de)
 *
 * Copyright 2007 Sebastian Lenz
 * 
 * This file is part of parsemis.
 *
 * Licence: 
 *  LGPL: http://www.gnu.org/licenses/lgpl.html
 *   EPL: http://www.eclipse.org/org/documents/epl-v10.php
 *   See the LICENSE file in the project's top-level directory for details.
 */
package com.kb.miner.tools;

import com.kb.miner.graph.Graph;
import com.kb.miner.graph.GraphFactory;
import com.kb.miner.graph.ListGraph;
import com.kb.miner.miner.environment.Settings;
import com.kb.miner.parsers.GraphParser;
import com.kb.miner.parsers.LabelParser;
import com.kb.miner.parsers.StringLabelParser;
import com.kb.miner.utils.GraphInformation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

/**
 * 
 * @author Sebastian Lenz (siselenz@stud.informatik.uni-erlangen.de)
 * @author Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 * 
 */
public class Graphtest {

	private static GraphParser<String, String> gp;

	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(final String[] args) throws FileNotFoundException,
			IOException, ParseException {
		final Collection<Graph<String, String>> graphs = parseFile(args[0]);

		if (graphs.isEmpty()) {
			System.out.println("keine graphen");
		} else {
			GraphInformation.getInfo(graphs, args[1]);
		}
	}

	@SuppressWarnings("unchecked")
	private static Collection<Graph<String, String>> parseFile(
			final String filename) throws FileNotFoundException, IOException,
			ParseException {
		InputStream in = new FileInputStream(filename);
		if (filename.endsWith(".gz")) {
			in = new GZIPInputStream(in);
		}

		final LabelParser<String> lp = new StringLabelParser();
		final GraphFactory<String, String> factory = new ListGraph.Factory<String, String>(
				lp, lp);
		gp = Settings.parseFileName(filename, lp, lp);
		return (gp == null) ? new HashSet<Graph<String, String>>() : gp.parse(
				in, factory);
	}

}
