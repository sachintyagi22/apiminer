package com.kb.java.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kb.java.graph.*;
import com.kb.java.parse.CFGResolver;
import com.kb.java.parse.JavaASTParser;
import com.kb.ml.DAGClusterMatric;
import com.kb.ml.KMedoids;
import de.parsemis.Miner;
import de.parsemis.graph.Graph;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.general.DataBaseGraph;
import de.parsemis.miner.general.Fragment;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.EdgeNameProvider;
import org.jgrapht.ext.VertexNameProvider;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DirectedGraphUnion;
import org.jgrapht.graph.builder.DirectedWeightedGraphBuilder;
import org.jgrapht.util.WeightCombiner;

import java.io.*;
import java.util.*;

public class Clusterer {

    static final Map<String, String> idMap = new HashMap<>();
    static int innerIdCounter = 0;
	//private static final String FILE_BASE_PATH = "/tmp/java.txt";
	//private static final String FILE_BASE_PATH = "/home/jatina/20files";
	private static final String FILE_BASE_PATH = "/home/jatina/apiminer/fileniochannels";
	private static final double MIN_FREQ = 0.02;
	public static final int N_CLUSTERS = 4;
	public static final String FILTERED_STRING = "java.nio.channels.FileChannel";
	private static boolean prune = false;
	private static int minInstanceSize = 4;

	public static void main(String[] args) throws IOException {

		long s = System.currentTimeMillis();
		Map<String, NamedDirectedGraph> instances = getGraphInstances(FILE_BASE_PATH);
        long graphReadTime = System.currentTimeMillis();
		List<List<NamedDirectedGraph>> clusters = getClusters(instances.values(), N_CLUSTERS);

        //List<Collection<NamedDirectedGraph>> clusters = new ArrayList<Collection<NamedDirectedGraph>>();
        //clusters.add(instances.values());
        long clusterTime = System.currentTimeMillis();

        VertexNameProvider<Node> vertexNameProvider = new VertexNameProvider<Node>() {
			@Override
			public String getVertexName(Node vertex) {
				return /* vertex.getId() + " : " + */vertex.getLabel();
			}
		};

        VertexNameProvider<Node> vertexIdProvider = new VertexNameProvider<Node>() {
            @Override
			public String getVertexName(Node vertex) {
                String id = idMap.get(vertex.getLabel());
                if(id == null){
                    id = String.valueOf(innerIdCounter++);
                    idMap.put(vertex.getLabel(), id);
                }
				return id;
			}
		};

        EdgeNameProvider<DirectedEdge> edgeEdgeNameProvider = new EdgeNameProvider<DirectedEdge>() {
            @Override
            public String getEdgeName(DirectedEdge edge) {
                return String.valueOf(edge.getWeight());
            }
        };

        DOTExporter<Node, DirectedEdge> exporter = new DOTExporter<>(
				vertexIdProvider, vertexNameProvider, edgeEdgeNameProvider);

        int clusterIndex = 0;
        NamedDirectedGraph current = new NamedDirectedGraph();
        int i = 0;

        for(List<NamedDirectedGraph> cluster: clusters){
            int count = 0;
            for(NamedDirectedGraph g : cluster){
                if(g.vertexSet().size() < minInstanceSize){
                    System.out.println("Skipping...");
                    continue;
                }
                i++;
                count ++;
                if(i % 10 == 0){
                    System.out.println("Processed graphs :" + i);
                }

                if(count % 100 == 0){
                    trim(current, count * 0.02D);
                }
                current = mergeGraphs(current, g);
            }
            trim(current, count * 0.05);
            File f = new File("/home/jatina/apiminer/cluster"+ clusterIndex++ +".dot");
            if(f.exists()){
                f.delete();
            }
            exporter.export(new PrintWriter(new FileOutputStream(f)), current);
        }

        System.out.println("Total graphs : " + i);

        long trimTime = System.currentTimeMillis();
        long patternTime = System.currentTimeMillis();


        System.out.println("Graph read time: " + (graphReadTime - s));
        System.out.println("Clustering time: " + (clusterTime - graphReadTime));
        System.out.println("Pattern time: " + (patternTime - clusterTime));
        System.out.println("Pattern time: " + (trimTime - patternTime));

        //mineGraphDB(instances, clusters, exporter);

    }

    private static void trim(NamedDirectedGraph current, double support) {
        Set<DirectedEdge> edgesToRemove = new HashSet<>();
        for(DirectedEdge e : current.edgeSet()){
            if(e.getWeight() < support){
                edgesToRemove.add(e);
            }
        }

        for(DirectedEdge e: edgesToRemove){
            current.removeEdge(e);
        }

        Set<Node> nodesToRemove = new HashSet<>();
        for(Node n : current.vertexSet()){
            if(current.inDegreeOf(n)==0 && current.outDegreeOf(n)==0){
                nodesToRemove.add(n);
            }
        }
        for(Node n : nodesToRemove){
            current.removeVertex(n);
        }
    }


    private static NamedDirectedGraph mergeGraphs(NamedDirectedGraph digraph1, NamedDirectedGraph digraph2) {

        DirectedGraphUnion<Node, DirectedEdge> du = new DirectedGraphUnion<>(digraph1, digraph2, WeightCombiner.SUM);
        DirectedWeightedGraphBuilder<Node, DirectedEdge, NamedDirectedGraph> dwgb = new DirectedWeightedGraphBuilder<Node, DirectedEdge, NamedDirectedGraph>(new NamedDirectedGraph());

        for(DirectedEdge e : du.edgeSet()){
            LabelNode src = e.getSource();
            LabelNode target = e.getTarget();
            if(src.getLabel().contains(FILTERED_STRING) || target.getLabel().contains(FILTERED_STRING)){
                double w = du.getEdgeWeight(e);
                dwgb.addEdge(src, target, w);
            }
        }
        return dwgb.build();
    }


    private static void mineGraphDB(Map<String, NamedDirectedGraph> instances, List<Collection<NamedDirectedGraph>> clusters, DOTExporter<Node, DirectedEdge> exporter) {
        try {
			int clusterCount = 0;
			for (Collection<NamedDirectedGraph> cluster : clusters) {
				String fileName = "cluster" + (++clusterCount) + ".dot";

				File clusterFile = new File(fileName);
				if (clusterFile.exists()) {
					clusterFile.delete();
				}

				FileWriter dotFileWriter = new FileWriter(fileName, true);
				/*int minFreq = (int) (cluster.size() * MIN_FREQ);
				if (minFreq < 10) {
					minFreq = 10;
				}*/
				int minFreq = 15;
				for (NamedDirectedGraph graph : cluster) {
					StringWriter stringWriter = new StringWriter();
					graph.vertexSet();
					graph.edgeSet();
					exporter.export(stringWriter, graph);

					String graphString = stringWriter.getBuffer().toString();
					System.out.println(graph.getId()  + " : " + graph.getLabel());
					graphString = StringUtils.replace(graphString,
                            "digraph G {", "digraph " + graph.getId() + " {");
					dotFileWriter.append(graphString + "\n\n");
					dotFileWriter.flush();

					stringWriter.close();
				}
				dotFileWriter.flush();
				dotFileWriter.close();

				String parseMisArgs[] = new String[] {
						"--graphFile=" + fileName,
						"--outputFile=digraphResults_" + clusterCount,
						"--algorithm=gspan", "--minimumFrequency=" + minFreq,
						"--distribution=threads", "--threads=4",
						"--minimumNodeCount=3", "--minimumEdgeCount=3" /*, "--closeGraph=true"*/ }; // "--storeHierarchicalEmbeddings=true",
																			// "--embeddingBased=trueexit"
				System.out.println("Cluster Size is: " + cluster.size()
						+ " , Minimum Freq: " + minFreq);
				final Settings settings = Settings.parse(parseMisArgs);
				run(settings, instances, exporter);

				Miner.startParsemis(parseMisArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private static <NodeType, EdgeType> void run(final Settings<NodeType, EdgeType> settings, Map<String, NamedDirectedGraph> instances, DOTExporter<Node,DirectedEdge> exporter) {
		final Collection<Graph<NodeType, EdgeType>> graphs = Miner.parseInput(settings);
		final Collection<Fragment<NodeType, EdgeType>> frags = Miner.mine(graphs,
				settings);
		Miner.printOutput(frags, settings);
		for(Fragment frag : frags){
			final Iterator<DataBaseGraph<NodeType, EdgeType>> git = frag.graphIterator();
			if (git.hasNext()) {
				Graph<NodeType, EdgeType> supportingGraph = git.next().toGraph();
				NamedDirectedGraph concreteGraph = instances.get(supportingGraph.getName());
				
				concreteGraph = pruneConcreteGraph(concreteGraph, frag);
				exporter.export(new PrintWriter(System.out), concreteGraph);
			}
		}
	}

	private static NamedDirectedGraph pruneConcreteGraph(NamedDirectedGraph concreteGraph, Fragment frag) {
		Graph graph = frag.toGraph();
		int nodeCount = graph.getNodeCount();
		List<String> nodesInFragment = new ArrayList<String>();
		for(int i=0; i < nodeCount; i++){
			de.parsemis.graph.Node node = graph.getNode(i);
			nodesInFragment.add(node.getLabel().toString());
		}
		
		Set<Node> nodesToRemove = new HashSet<Node>(); 
		for(Node v : concreteGraph.vertexSet()){
			if(v instanceof InvocationNode){
				if(!nodesInFragment.contains(v.getLabel())){
					nodesToRemove.add(v);
					
				}
			}
			
			
			/*if(v instanceof ControlStructureNode){
				concreteGraph.incomingEdgesOf(v);
				for(DirectedEdge in : concreteGraph.incomingEdgesOf(v)){
				
				}
				concreteGraph.outgoingEdgesOf(v);
			}*/
		}
		
		for(Node n : nodesToRemove){
			concreteGraph.removeVertex(n);
		}
		return concreteGraph;
	}

	private static List<List<NamedDirectedGraph>> getClusters(
			Collection<NamedDirectedGraph> instances, int n) {
		DAGClusterMatric dagClusterMatric = new DAGClusterMatric(
				FILTERED_STRING, instances.size());
		KMedoids<NamedDirectedGraph> kMedoids = new KMedoids<>(
				dagClusterMatric, n);
		long start = System.currentTimeMillis();
		kMedoids.buildClusterer(new ArrayList<NamedDirectedGraph>(instances));
		long end = System.currentTimeMillis();
		int total = dagClusterMatric.getHits() + dagClusterMatric.getMiss();
		System.out.println("Time taken for clustering : " + instances.size()
				+ " graphs was " + (end - start)
				+ "mili secs, Cache hit ratio : " + dagClusterMatric.getHits()
				* 100D / total + ", Cache size: " + dagClusterMatric.getMiss());
		// kMedoids.printClusters(instances);

		List<List<NamedDirectedGraph>> clusters = kMedoids
				.getClusters(instances);
		return clusters;
	}

	private static Map<String, NamedDirectedGraph> getGraphInstances(String path) throws IOException {

		Map<String, NamedDirectedGraph> instances = new HashMap<String, NamedDirectedGraph>();
		File f = new File(path);
		List<File> listOfSourceFiles = new ArrayList<File>();
		findJavaFiles(f, listOfSourceFiles);
		System.out.println("Total java files : " + listOfSourceFiles.size());
		if(f.isDirectory()){
			writeToFile(listOfSourceFiles);
		}
		Integer i = 0;

		if (listOfSourceFiles != null && listOfSourceFiles.size() > 0) {
			for (File sourceFile : listOfSourceFiles) {
				try {
					List<DirectedGraph<Node, DirectedEdge>> graphs = getGraphsFromFile(sourceFile);
					Iterable<DirectedGraph<Node, DirectedEdge>> filteredGraphs = filterGraphs(graphs);

					for (DirectedGraph<Node, DirectedEdge> g : filteredGraphs) {
						String name = "";
						for (Node n : g.vertexSet()) {
							if (n.getLabel().contains("ROOT:")) {
								name = StringUtils.substringAfter(n.getLabel(),
										"ROOT:");
								break;
							}
						}
						
						if(prune ){
							prune(g);
						}
						
						int size = g.vertexSet().size();
						if (size > minInstanceSize ) {
							instances.put(i.toString(), new NamedDirectedGraph(g, i
									.toString(), sourceFile.getAbsolutePath()
									+ ":" + name));
						}
						i++;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return instances;
	}

	private static void writeToFile(List<File> listOfSourceFiles) throws IOException {
		String path = "/tmp/java.txt";
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		
		FileWriter fWriter = new FileWriter(path, true);
		for(File f : listOfSourceFiles){
			fWriter.append(f.getPath() + "\n");
		}
		fWriter.close();
	}

	private static void findJavaFiles(File file, List<File> files) throws IOException {
		File[] listOfSourceFiles = file.listFiles();
		
		if(file.isFile()){
			BufferedReader br = new BufferedReader(new java.io.InputStreamReader(new FileInputStream(file)));
			while(br.ready()){
				files.add(new File(br.readLine()));
			}
			br.close();
			return;
		}
		
		if(listOfSourceFiles!=null && listOfSourceFiles.length > 0){
			for(File f : listOfSourceFiles){
				if(f.isDirectory()){
					findJavaFiles(f, files);
				}else if(f.getPath().endsWith("java")) {
					files.add(f);
				}
			}
			
		}
	}

	private static void prune(DirectedGraph<Node, DirectedEdge> g) {

		List<Node> verticesToRemove = new ArrayList<Node>();
		for (Node n : g.vertexSet()) {
			if (n instanceof InvocationNode) {
				if (!n.getLabel().contains(FILTERED_STRING)) {
					for (DirectedEdge e : g.edgesOf(n)) {
						Node src = (Node) e.getSource();
						Node target = (Node) e.getTarget();
						if ((src != null && src.getLabel().contains(
								FILTERED_STRING))
								|| (target != null && target.getLabel()
										.contains(FILTERED_STRING))) {
							continue;
						}
					}
					verticesToRemove.add(n);
				}
			}
		}

		for (Node n : verticesToRemove) {
			g.removeVertex(n);

		}
	}

	private static Iterable<DirectedGraph<Node, DirectedEdge>> filterGraphs(
			List<DirectedGraph<Node, DirectedEdge>> graphs) {
		Iterable<DirectedGraph<Node, DirectedEdge>> filteredGraphs = Iterables
				.filter(graphs,
						new Predicate<DirectedGraph<Node, DirectedEdge>>() {
							@Override
							public boolean apply(
									DirectedGraph<Node, DirectedEdge> g) {
								for (Node n : g.vertexSet()) {
									boolean contains = n.getLabel().contains(
											FILTERED_STRING);
									if (contains)
										return true;
								}
								return false;
							}
						});
		return filteredGraphs;
	}

	private static List<DirectedGraph<Node, DirectedEdge>> getGraphsFromFile(
			File sourceFile) throws FileNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(sourceFile);
		String fileContent = readInputStream(fileInputStream);

		CFGResolver cfgResolver = new CFGResolver();
		JavaASTParser pars = new JavaASTParser(true);
		ASTNode cu = pars.getAST(fileContent,
				JavaASTParser.ParseType.COMPILATION_UNIT);
		cu.accept(cfgResolver);

		List<DirectedGraph<Node, DirectedEdge>> graphs = cfgResolver
				.getMethodCFGs();
		return graphs;
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
