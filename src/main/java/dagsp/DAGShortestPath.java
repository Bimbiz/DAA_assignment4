package dagsp;

import graph.DirectedGraph;
import graph.Edge;
import topo.TopoResult;
import metrics.MetricsInterface;

import java.util.*;

/**
 * Single-source shortest paths in a DAG.
 * Instrumented with MetricsInterface to count relaxations and timing.
 */

public class DAGShortestPath {
    private final DirectedGraph graph;
    private final MetricsInterface metrics;

    public DAGShortestPath(DirectedGraph graph, MetricsInterface metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public PathResult run(int source, TopoResult topo) {
        long t0 = System.nanoTime(); // start timing

        int n = graph.size();
        long INF = Long.MAX_VALUE / 4;
        long[] dist = new long[n];
        int[] parent = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        // Precompute order positions
        List<Integer> order = topo.getOrder();

        // Relax edges following topological order
        for (int u : order) {
            if (dist[u] == INF) continue;
            for (Edge e : graph.neighbors(u)) {
                long newDist = dist[u] + e.weight;
                if (newDist < dist[e.to]) {
                    dist[e.to] = newDist;
                    parent[e.to] = u;
                    metrics.inc("dagsp.relaxations"); // count each relaxation
                }
            }
        }

        long t1 = System.nanoTime();
        metrics.addTime("time.dagsp.shortest", t1 - t0);

        return new PathResult(dist, parent, source);
    }
}
