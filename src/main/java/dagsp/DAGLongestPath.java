package dagsp;

import graph.DirectedGraph;
import graph.Edge;
import topo.TopoResult;
import metrics.MetricsInterface;

import java.util.*;

/**
 * Finds the longest paths in a DAG using dynamic programming
 * over a given topological order.
 * Tracks relaxations and total time using MetricsInterface.
 */

public class DAGLongestPath {
    private final DirectedGraph graph;
    private final MetricsInterface metrics;

    public DAGLongestPath(DirectedGraph graph, MetricsInterface metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public PathResult run(int source, TopoResult topo) {
        long t0 = System.nanoTime(); // start timing

        int n = graph.size();
        long NEG = Long.MIN_VALUE / 4;
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, NEG);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        // Relax edges in topological order
        for (int u : topo.getOrder()) {
            if (dist[u] == NEG) continue;
            for (Edge e : graph.neighbors(u)) {
                long newDist = dist[u] + e.weight;
                if (newDist > dist[e.to]) {
                    dist[e.to] = newDist;
                    parent[e.to] = u;
                    metrics.inc("dagsp.relaxations.longest"); // count each relaxation
                }
            }
        }

        long t1 = System.nanoTime();
        metrics.addTime("time.dagsp.longest", t1 - t0);

        return new PathResult(dist, parent, source);
    }
}
