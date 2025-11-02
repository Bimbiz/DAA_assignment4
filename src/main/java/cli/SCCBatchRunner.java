package cli;

import graph.DirectedGraph;
import scc.TarjanSCC;
import scc.SCCResult;
import topo.KahnTopological;
import topo.DFSTopological;
import topo.TopoResult;
import dagsp.DAGShortestPath;
import dagsp.DAGLongestPath;
import dagsp.PathResult;
import utils.JSONReader;
import utils.CSVExporter;
import metrics.MetricsInterface;
import metrics.DefaultMetrics;

import java.io.File;
import java.util.*;

/**
 * Batch runner that processes all .json datasets inside /data folder.
 * Results are collected into a single summary.csv file.
 */
public class SCCBatchRunner {
    public static void main(String[] args) {
        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.err.println("data/ directory not found.");
            return;
        }

        File[] jsonFiles = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (jsonFiles == null || jsonFiles.length == 0) {
            System.err.println("No JSON datasets found in data/");
            return;
        }

        CSVExporter csv = new CSVExporter("data/results/batch_summary.csv");
        csv.writeHeader("dataset", "nodes", "edges", "sccCount", "timeSCC(ns)", "timeTopo(ns)", "timeSP(ns)", "timeLP(ns)");

        for (File file : jsonFiles) {
            System.out.println("Processing " + file.getName());

            try {
                DirectedGraph g = JSONReader.readGraph(file.getPath());
                MetricsInterface metrics = new DefaultMetrics();

                //SCC
                long sccStart = System.nanoTime();
                TarjanSCC t = new TarjanSCC(g, metrics);
                SCCResult scc = t.run();
                long sccTime = System.nanoTime() - sccStart;

                //Condensation graph
                int C = scc.numComponents();
                DirectedGraph cond = new DirectedGraph(C);
                List<List<Integer>> origAdj = new ArrayList<>();
                for (int i = 0; i < g.size(); i++) {
                    List<Integer> list = new ArrayList<>();
                    for (var e : g.neighbors(i)) list.add(e.to);
                    origAdj.add(list);
                }

                List<Set<Integer>> cadj = scc.buildCondensation(g.size(), origAdj);
                for (int u = 0; u < cadj.size(); u++) {
                    for (int v : cadj.get(u)) cond.addEdge(u, v, 1L);
                }

                // --- Topological Sort ---
                long topoStart = System.nanoTime();
                TopoResult topo;
                try {
                    KahnTopological kahn = new KahnTopological(cond, metrics);
                    topo = kahn.run();
                } catch (IllegalStateException ex) {
                    DFSTopological dfsTop = new DFSTopological(cond);
                    topo = dfsTop.run();
                }
                long topoTime = System.nanoTime() - topoStart;

                // --- DAG Shortest Path ---
                long spStart = System.nanoTime();
                int sourceComp = scc.getComponentId(g.getSource());
                DAGShortestPath sp = new DAGShortestPath(cond, metrics);
                PathResult spr = sp.run(sourceComp, topo);
                long spTime = System.nanoTime() - spStart;

                // --- DAG Longest Path ---
                long lpStart = System.nanoTime();
                DAGLongestPath lp = new DAGLongestPath(cond, metrics);
                PathResult lpr = lp.run(sourceComp, topo);
                long lpTime = System.nanoTime() - lpStart;

                csv.writeRow(
                        file.getName(),
                        g.size(),
                        g.edgeCount(),
                        scc.numComponents(),
                        sccTime,
                        topoTime,
                        spTime,
                        lpTime
                );

                System.out.println("Done: " + file.getName());

            } catch (Exception ex) {
                System.err.println("Failed on " + file.getName() + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        System.out.println("\nBatch run complete!");
        System.out.println("Results saved to data/results/batch_summary.csv");
    }
}
