package topo;

import graph.DirectedGraph;
import graph.Edge;
import metrics.MetricsInterface;

import java.util.*;

/**
 * Kahn's algorithm for topological sorting.
 * Tracks queue pops/pushes and edge relaxations using MetricsInterface.
 */

public class KahnTopological {
    private final DirectedGraph graph;
    private final MetricsInterface metrics;

    public KahnTopological(DirectedGraph graph, MetricsInterface metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public TopoResult run() {
        int n = graph.size();
        int[] indeg = new int[n];

        // Compute indegree for all vertices
        for (int u = 0; u < n; u++) {
            for (Edge e : graph.neighbors(u)) {
                indeg[e.to]++;
            }
        }

        // Add all nodes with indegree 0 to queue
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (indeg[i] == 0) {
                q.add(i);
                metrics.inc("topo.kahn.pushes"); // count initial pushes
            }
        }

        List<Integer> order = new ArrayList<>();
        long t0 = System.nanoTime();

        // Main loop
        while (!q.isEmpty()) {
            int u = q.remove();
            metrics.inc("topo.kahn.pops");
            order.add(u);

            for (Edge e : graph.neighbors(u)) {
                metrics.inc("topo.kahn.relaxations");
                indeg[e.to]--;
                if (indeg[e.to] == 0) {
                    q.add(e.to);
                    metrics.inc("topo.kahn.pushes");
                }
            }
        }

        long t1 = System.nanoTime();
        metrics.addTime("time.topo", t1 - t0);

        if (order.size() != n) {
            throw new IllegalStateException("Graph is not a DAG (cycle detected)");
        }

        return new TopoResult(order);
    }
}
