/**
 * Created on Jul 06, 2007
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
package com.kb.miner.visualisation.gui;

import com.kb.miner.chemical.Atom;
import com.kb.miner.graph.Graph;
import com.kb.miner.utils.GraphUtils;
import com.kb.miner.visualisation.GraphPanel;
import com.kb.miner.visualisation.SugiyamaDemo;
import com.kb.miner.visualisation.chemicalVisualisation.Demo;
import com.kb.miner.visualisation.prefuseVisualisation.PrefuseDemo;

import javax.swing.*;
import java.awt.*;

/**
 * @author Marc Woerlein (woerlein@informatik.uni-erlangen.de)
 * 
 */
public class GraphPanelGenerator {

	public static <NodeType, EdgeType> GraphPanel createPanel(
			final Dimension d, final Graph<NodeType, EdgeType> g,
			final boolean isFragment) {
		return createPanel(d, g, isFragment, null);

	}

	@SuppressWarnings("unchecked")
	public static <NodeType, EdgeType> GraphPanel createPanel(
			final Dimension d, final Graph<NodeType, EdgeType> g,
			final boolean isFragment, final JComponent propertyChanger) {
		GraphPanel demo;
		if (g.getNode(0).getLabel() instanceof Atom) {
			demo = new Demo(g, d, isFragment);
		} else if (GraphUtils.isDAG(g)) {
			demo = new SugiyamaDemo(d, g, isFragment);
			((SugiyamaDemo) demo).zoomToFit();
		} else {
			demo = new PrefuseDemo(d, g, isFragment);
		}
		demo.addToPropertyChangeListener(propertyChanger);
		return demo;
	}

}
