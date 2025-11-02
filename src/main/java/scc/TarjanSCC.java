package scc;

import java.util.*;
import graph.Edge;
import graph.DirectedGraph;
import metrics.MetricsInterface;

/**
 * Tarjan's algorithm for finding strongly connected components.
 * Instrumented with MetricsInterface counters and timings.
 */

public class TarjanSCC {

    private final MetricsInterface metrics;
    private final DirectedGraph graph;
    private final int n;

    private int time = 0;
    private final int[] disc;
    private final int[] low;
    private final boolean[] onStack;
    private final Deque<Integer> stack;
    private final List<List<Integer>> components;

    public TarjanSCC(DirectedGraph graph, MetricsInterface metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.n = graph.size();
        this.disc = new int[n];
        Arrays.fill(disc, -1);
        this.low = new int[n];
        this.onStack = new boolean[n];
        this.stack = new ArrayDeque<>();
        this.components = new ArrayList<>();
    }

    public SCCResult run() {
        long t0 = System.nanoTime();

        for (int v = 0; v < n; v++) {
            if (disc[v] == -1) dfs(v);
        }

        long elapsed = System.nanoTime() - t0;
        metrics.addTime("time.scc", elapsed);

        return new SCCResult(components, n);
    }

    private void dfs(int u) {
        metrics.inc("scc.dfs.visits");

        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;

        for (Edge e : graph.neighbors(u)) {
            metrics.inc("scc.dfs.edges");
            int v = e.to;

            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            // root of SCC
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int w = stack.pop();
                onStack[w] = false;
                comp.add(w);
                if (w == u) break;
            }
            components.add(comp);
            metrics.inc("scc.components");
        }
    }
}
