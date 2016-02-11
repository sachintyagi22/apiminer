package com.kb.java.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import de.parsemis.Miner;
import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.NamedDirectedGraph;
import com.kb.java.graph.Node;
import com.kb.java.parse.CFGResolver;
import com.kb.java.parse.JavaASTParser;
import com.kb.ml.DAGClusterMatric;
import com.kb.ml.KMedoids;

public class Clusterer {

	public static final String FILTERED_STRING = "java.io.BufferedReader";

	public static void main(String[] args) {

		String tmpFile = System.getProperty("java.io.tmpdir");

		List<NamedDirectedGraph> instances = new ArrayList<>();
		File f = new File("/home/jatina/apiminer/sourceJavaFiles/");
		File[] listOfSourceFiles = f.listFiles();
		Integer i = 0;

		if (listOfSourceFiles != null && listOfSourceFiles.length > 0) {
			for (File sourceFile : listOfSourceFiles) {
				try {
					FileInputStream fileInputStream = new FileInputStream(sourceFile);
					String fileContent = readInputStream(fileInputStream);

					CFGResolver cfgResolver = new CFGResolver();
					JavaASTParser pars = new JavaASTParser(true);
					ASTNode cu = pars.getAST(fileContent, JavaASTParser.ParseType.COMPILATION_UNIT);
					cu.accept(cfgResolver);

					List<DirectedGraph<Node, DirectedEdge>> graphs = cfgResolver.getMethodCFGs();

					Iterable<DirectedGraph<Node, DirectedEdge>> filteredGraphs = Iterables
							.filter(graphs,
									new Predicate<DirectedGraph<Node, DirectedEdge>>() {
										@Override
										public boolean apply(DirectedGraph<Node, DirectedEdge> g) {
											for(Node n : g.vertexSet()){
												boolean contains = n.getLabel().contains(FILTERED_STRING);
												if(contains) return true;
											}
											return false;
										}
									});

					for(DirectedGraph<Node, DirectedEdge> g : filteredGraphs){
						String name = "";
						for(Node n : g.vertexSet()){
							if(n.getLabel().contains("ROOT:")){
								name = StringUtils.substringAfter(n.getLabel(), "ROOT:");
								break;
							}
						}
						instances.add(new NamedDirectedGraph(g, i.toString(), sourceFile.getAbsolutePath() + ":" +  name));
						i++;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		DAGClusterMatric dagClusterMatric = new DAGClusterMatric(FILTERED_STRING, instances.size());
		KMedoids<NamedDirectedGraph> kMedoids = new KMedoids<>(dagClusterMatric, 11);
		long start = System.currentTimeMillis();
		kMedoids.buildClusterer(instances);
		long end = System.currentTimeMillis();
		int total =  dagClusterMatric.getHits() + dagClusterMatric.getMiss();
		System.out.println("Time taken for clustering : " + instances.size() + " graphs was " + (end - start)
				+ "mili secs, Cache hit ratio : " + dagClusterMatric.getHits() * 100D / total + ", Cache size: " + dagClusterMatric.getMiss());
		kMedoids.printClusters(instances);

		List<List<NamedDirectedGraph>> clusters = kMedoids.getClusters(instances);

		VertexNameProvider<Node> vertexNameProvider = new VertexNameProvider<Node>() {
			@Override
			public String getVertexName(Node vertex) {
				return /*vertex.getId() + " : " +*/ vertex.getLabel();
			}
		};

		VertexNameProvider<Node> vertexIdProvider = new VertexNameProvider<Node>() {
			@Override
			public String getVertexName(Node vertex) {
				return String.valueOf(vertex.getId());
			}
		};

		DOTExporter<Node, DirectedEdge> exporter = new DOTExporter<>(vertexIdProvider, vertexNameProvider, null);

		try {


			int clusterCount = 0;
			for (List<NamedDirectedGraph> cluster : clusters) {
				String fileName = "cluster"+(++clusterCount)+".dot";

				File clusterFile = new File(fileName);
				if (clusterFile.exists()) {
					clusterFile.delete();
				}

				FileWriter dotFileWriter = new FileWriter(fileName, true);
				int minFreq = (int)(cluster.size()*0.15);
				if(minFreq <= 5) { minFreq=6;}
				for (DirectedGraph<Node, DirectedEdge> graph : cluster) {
					StringWriter stringWriter = new StringWriter();
					graph.vertexSet();
					graph.edgeSet();
					exporter.export(stringWriter, graph);

					// if(stringWriter.getBuffer().toString().contains(FILTERED_STRING)){
					dotFileWriter.append(stringWriter.getBuffer().toString()
							+ "\n\n");
					dotFileWriter.flush();
					// }

					stringWriter.close();
				}
				dotFileWriter.flush();
				dotFileWriter.close();

				String parseMisArgs[] = new String[]{
						"--graphFile="+fileName, "--outputFile=digraphResults_"+clusterCount,
						"--algorithm=gspan", "--minimumFrequency="+minFreq, "--distribution=threads", "--threads=4",
						"--minimumNodeCount=3",	"--minimumEdgeCount=3"
				}; //"--storeHierarchicalEmbeddings=true", "--embeddingBased=trueexit"
				System.out.println("Cluster Size is: "+cluster.size()+" , Minimum Freq: "+minFreq);
				Miner.startParsemis(parseMisArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected static String readInputStream(InputStream is) {
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
