package com.kb.java.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kb.java.graph.DirectedEdge;
import com.kb.java.graph.Node;
import com.kb.java.parse.CFGResolver;
import com.kb.java.parse.JavaASTParser;
import com.kb.ml.DAGClusterMatric;
import com.kb.ml.KMedoids;
import org.eclipse.jdt.core.dom.ASTNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.VertexNameProvider;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConcreteUseClusterer {

    public static List<List<ConcreteUse>> cluster(List<ConcreteUse> concreteUses) {

		/*KMedoids<List<CFNode>> kmedoids = new KMedoids<List<CFNode>>(new KendallsTauDistanceMetric<CFNode>(), 2);
		
		List<List<CFNode>> instances = new ArrayList<List<CFNode>>();
		for (ConcreteUse c : concreteUses) {
			List<CFNode> l1 = c.getCFG().asList();
			instances.add(l1);
		}

		kmedoids.buildClusterer(instances);
		kmedoids.printClusters(instances);
		// TODO Auto-generated method stub
		return new LinkedList<>();*/
        KMedoids<DirectedGraph<Node, DirectedEdge>> kMedoids = new KMedoids<>(new DAGClusterMatric(), 2);

        List<DirectedGraph<Node, DirectedEdge>> instances = new ArrayList<>();
        File f = new File("./fileniochannels");
        File[] listOfSourceFiles = f.listFiles();
        //int count = 7;
        if (listOfSourceFiles != null && listOfSourceFiles.length > 0) {
            for (File sourceFile : listOfSourceFiles) {
          //      if(count <= 0){
            //        break;
              //  }

                try {

                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    String fileContent = readInputStream(fileInputStream);

                    CFGResolver cfgResolver = new CFGResolver();
                    JavaASTParser pars = new JavaASTParser(true);
                    ASTNode cu = pars.getAST(fileContent, JavaASTParser.ParseType.COMPILATION_UNIT);
                    cu.accept(cfgResolver);

                    List<DirectedGraph<Node, DirectedEdge>> graphs = cfgResolver.getMethodCFGs();

                    Iterable<DirectedGraph<Node, DirectedEdge>> filteredNodes =
                            Iterables.filter(graphs, new Predicate<DirectedGraph<Node, DirectedEdge>>() {

                                private boolean isBufferedReader(Graph<Node, DirectedEdge> graph) {
                                    Iterable nodes = Iterables.filter(graph.vertexSet(), new Predicate<Node>() {
                                        @Override
                                        public boolean apply(Node node) {
                                            return node.getLabel().contains("FileChannel");
                                        }
                                    });
                                    return Lists.newArrayList(nodes).size() > 0;
                                }

                                @Override
                                public boolean apply(DirectedGraph<Node, DirectedEdge> nodeDirectedEdgeDirectedGraph) {
                                    return isBufferedReader(nodeDirectedEdgeDirectedGraph);
                                }

                            });

                instances.addAll(Lists.newArrayList(filteredNodes));
            }catch(Exception e){
                e.printStackTrace();
            }
//                count--;
        }
    }



    kMedoids.buildClusterer(instances);
    kMedoids.printClusters(instances);

        List<List<DirectedGraph<Node,DirectedEdge>>> clusters = kMedoids.getClusters(instances);
        VertexNameProvider<Node> vertexNameProvider = new VertexNameProvider<Node>() {
            @Override
            public String getVertexName(Node vertex) {
                return vertex.getId() + " : " + vertex.getLabel();
            }
        };

        VertexNameProvider<Node> vertexIdProvider = new VertexNameProvider<Node>() {
            @Override
            public String getVertexName(Node vertex) {
                return String.valueOf(vertex.getId());
            }
        };
        DOTExporter<Node, DirectedEdge> exporter = new DOTExporter<>(vertexIdProvider, vertexNameProvider , null);

        try{
            File clusterFile = new File("cluster.dot");
            if(clusterFile.exists()){
                clusterFile.delete();
            }

            int clusterCount =0;
            for (List<DirectedGraph<Node,DirectedEdge>> cluster : clusters) {
                FileWriter dotFileWriter = new FileWriter("cluster.dot",true);

                dotFileWriter.append("============================"+(++clusterCount)+"==================================\n");
                for(DirectedGraph<Node,DirectedEdge> graph : cluster) {
                    StringWriter stringWriter = new StringWriter();
                    graph.vertexSet();
                    graph.edgeSet();
                    exporter.export(stringWriter, graph);

                    if(stringWriter.getBuffer().toString().contains("BufferedReader")){
                        dotFileWriter.append(stringWriter.getBuffer().toString()+"\n\n");
                        dotFileWriter.flush();
                    }

                    stringWriter.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        return Collections.emptyList();
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
