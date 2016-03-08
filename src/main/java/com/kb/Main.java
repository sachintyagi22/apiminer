package com.kb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.google.common.collect.TreeMultiset;
import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.NamedDirectedGraph;
import com.kb.java.graph.Node;
import com.kb.java.model.Cluster;
import com.kb.java.model.Clusterer;

public class Main {

	private final static Logger LOG = Logger.getLogger(Main.class);
	private static final String FILTERED_STRING = "java.nio.channels.FileChannel";
	private final static GraphUtils graphUtils = new GraphUtils();
	private final static Clusterer clusterer = new Clusterer();
	private static final Map<String, String> idMap = graphUtils.idMap;
	private static int innerIdCounter = graphUtils.innerIdCounter;

	public static void main(String[] args) throws Exception {
		int n = 4;
		double edgeSupport = 0.2;
		StopWatch sw = new StopWatch();
		sw.start();
		Collection<NamedDirectedGraph> graphs = getGraphInstances(args[0]).values();
		sw.split();
		LOG.debug("Read " + graphs.size() + " graphs in " + sw.getSplitTime() + " ms.");
		sw.reset();
		sw.start();
		List<Cluster<NamedDirectedGraph>> clusters = clusterer.getClusters(graphs, n, 0.7D/n);
		sw.split();
		LOG.debug("Clustered into " + clusters.size() + " clusters in " + sw.getSplitTime() + " ms.");
		int clusterIndex = 0;
		
        for(Cluster<NamedDirectedGraph> cluster: clusters){
        	NamedDirectedGraph current = new NamedDirectedGraph();
        	TreeMultiset<String> seedNames = TreeMultiset.create();
        	TreeMultiset<String> methodNames = TreeMultiset.create();
    
        	int count = 0;
            for(NamedDirectedGraph g : cluster.getInstances()){
                count++;
                seedNames.add(g.getSeedName());
                methodNames.add(g.getMethodName());
                current = graphUtils.mergeGraphs(current, g);
            }
			graphUtils.trim(current, count * edgeSupport);
        
            if(LOG.isDebugEnabled()){
            	LOG.debug("For cluster " + clusterIndex + ", most likely seed var names : " + graphUtils.getTopN(seedNames, 3));
            	LOG.debug("For cluster " + clusterIndex + ", method names : " + graphUtils.getTopN(methodNames, 3));
                LOG.debug("Mean for " + clusterIndex + " => " + cluster.getMean().getMethodName());
            }
            
            String patternFile = "/home/jatina/apiminer/pattern"+ clusterIndex +".dot";
            String concreteUseFile = "/home/jatina/apiminer/concrete"+ clusterIndex +".dot";
            
            graphUtils.saveToFile(current, patternFile);
            graphUtils.saveToFile(cluster.getMean(), concreteUseFile);
            
            clusterIndex++;
        }
	}

	private static Map<String, NamedDirectedGraph> getGraphInstances(String path)
			throws IOException {
		File f = new File(path);
		List<File> listOfSourceFiles = new ArrayList<File>();
		findJavaFiles(f, listOfSourceFiles);
		List<String> fileContents = new ArrayList<String>();
		for (File sourceFile : listOfSourceFiles) {
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			String fileContent = readInputStream(fileInputStream);
			fileContents.add(fileContent);
			
		}
		return getGraphInstancesForFiles(fileContents);
	}

	private static Map<String, NamedDirectedGraph> getGraphInstancesForFiles(
			List<String> sourceFiles) throws FileNotFoundException {

		Map<String, NamedDirectedGraph> instances = new HashMap<String, NamedDirectedGraph>();
		for (String fileContent : sourceFiles) {
			List<NamedDirectedGraph> graphs = graphUtils.getGraphsFromFile(
					fileContent, FILTERED_STRING);
//			Integer i = 0;
			graphUtils.getNamedDirectedGraphs(instances, graphs);
		}
		return instances;
	}



	private static void findJavaFiles(File file, List<File> files)
			throws IOException {
		File[] listOfSourceFiles = file.listFiles();

		if (file.isFile()) {
			BufferedReader br = new BufferedReader(
					new java.io.InputStreamReader(new FileInputStream(file)));
			while (br.ready()) {
				files.add(new File(br.readLine()));
			}
			br.close();
			return;
		}

		if (listOfSourceFiles != null && listOfSourceFiles.length > 0) {
			for (File f : listOfSourceFiles) {
				if (f.isDirectory()) {
					findJavaFiles(f, files);
				} else if (f.getPath().endsWith("java")) {
					files.add(f);
				}
			}

		}
	}

	private static String readInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuffer contents = new StringBuffer();
		try {
			br = new BufferedReader(new java.io.InputStreamReader(is));
			while (br.ready()) {
				contents.append(br.readLine() + "\n");
			}
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			System.err.println("ioexception: " + e);
			return "";
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return contents.toString();

	}
}
