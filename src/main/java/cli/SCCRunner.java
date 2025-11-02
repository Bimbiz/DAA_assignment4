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
import utils.DatasetGenerator;
import utils.CSVExporter;
import metrics.MetricsInterface;
import metrics.DefaultMetrics;

import java.util.*;

/**
 * Command-line runner for the Smart City Scheduler project.
 * Steps:
 *  1. Read graph from JSON
 *  2. Run Tarjan SCC
 *  3. Build condensation DAG
 *  4. Topological sort (Kahn or DFS)
 *  5. Shortest & Longest path on condensation
 *  6. Export results to summary CSV
 */

public class SCCRunner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("  java -jar target/smart-city-scheduler-1.0-SNAPSHOT-fat.jar data/small_1.json");
            System.out.println("  java -jar target/smart-city-scheduler-1.0-SNAPSHOT-fat.jar generate   # create datasets");
            return;
        }

        try {
            String input = args[0];

            //Generate datasets if requested
            if ("generate".equalsIgnoreCase(input)) {
                DatasetGenerator.generateAll("data");
                System.out.println("Datasets generated in /data/");
                return;
            }

            //Read JSON graph
            System.out.println("Reading graph from " + input);
            DirectedGraph g = JSONReader.readGraph(input);
            System.out.println(g);

            MetricsInterface metrics = new DefaultMetrics();

            //Run SCC (Tarjan)
            TarjanSCC t = new TarjanSCC(g, metrics);
            long sccStart = System.nanoTime();
            SCCResult scc = t.run();
            long sccTime = System.nanoTime() - sccStart;
            metrics.addTime("time.scc", sccTime);
            System.out.println("Found " + scc.numComponents() + " components");
            scc.print();

            //Build condensation graph
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

            System.out.println("Condensation graph:");
            System.out.println(cond);

            //Topological Sort
            TopoResult topo;
            long topoStart = System.nanoTime();
            try {
                KahnTopological kahn = new KahnTopological(cond, metrics);
                topo = kahn.run();
                metrics.addTime("time.topo", System.nanoTime() - topoStart);
            } catch (IllegalStateException ex) {
                System.out.println("Kahn failed (cycle?) - fallback to DFS topo");
                DFSTopological dfsTop = new DFSTopological(cond);
                topo = dfsTop.run();
                metrics.addTime("time.topo", System.nanoTime() - topoStart);
            }
            topo.print();

            //DAG Shortest & Longest Paths
            int source = g.getSource();
            int sourceComp = scc.getComponentId(source);
            System.out.println("Using source node " + source + " -> component " + sourceComp);

            long spStart = System.nanoTime();
            DAGShortestPath sp = new DAGShortestPath(cond, metrics);
            PathResult spr = sp.run(sourceComp, topo);
            long spTime = System.nanoTime() - spStart;
            metrics.addTime("time.dagsp.shortest", spTime);
            System.out.println("Shortest paths (component level):");
            spr.print();

            long lpStart = System.nanoTime();
            DAGLongestPath lp = new DAGLongestPath(cond, metrics);
            PathResult lpr = lp.run(sourceComp, topo);
            long lpTime = System.nanoTime() - lpStart;
            metrics.addTime("time.dagsp.longest", lpTime);
            System.out.println("Longest paths (component level):");
            lpr.print();

            //Find critical path (longest path)
            long best = Long.MIN_VALUE / 4;
            int bestNode = -1;
            long[] dist = lpr.getDist();
            for (int i = 0; i < dist.length; i++) {
                if (dist[i] > best && dist[i] != Long.MIN_VALUE / 4) {
                    best = dist[i];
                    bestNode = i;
                }
            }

            List<Integer> criticalPath = (bestNode >= 0) ? lpr.reconstruct(bestNode) : List.of();
            if (!criticalPath.isEmpty()) {
                System.out.println("Critical path (component IDs): " + criticalPath + " length=" + best);
            } else {
                System.out.println("No reachable nodes for longest path.");
            }

            //Print metrics summary
            metrics.printSummary();

            //Export results to CSV
            CSVExporter csv = new CSVExporter("data/results/summary.csv");
            csv.writeHeader("dataset", "nodes", "edges", "sccCount",
                    "time.scc", "time.topo", "time.dagsp.shortest", "time.dagsp.longest");

            csv.writeRow(
                    input,
                    g.size(),
                    g.edgeCount(),
                    scc.numComponents(),
                    metrics.getTime("time.scc"),
                    metrics.getTime("time.topo"),
                    metrics.getTime("time.dagsp.shortest"),
                    metrics.getTime("time.dagsp.longest")
            );

            System.out.println("Results exported to data/results/summary.csv");

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
