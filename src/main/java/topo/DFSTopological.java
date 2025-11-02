package topo;

import graph.DirectedGraph;
import graph.Edge;

import java.util.*;

public class DFSTopological {
    private final DirectedGraph graph;
    private final boolean[] vis;
    private final List<Integer> order;
    private boolean cycle = false;

    public DFSTopological(DirectedGraph graph) {
        this.graph = graph;
        this.vis = new boolean[graph.size()];
        this.order = new ArrayList<>();
    }

    public TopoResult run() {
        for (int i = 0; i < graph.size(); i++) {
            if (!vis[i]) dfs(i, new boolean[graph.size()]);
        }
        if (cycle) throw new IllegalStateException("Cycle detected during DFS topo");
        Collections.reverse(order);
        return new TopoResult(order);
    }

    private void dfs(int u, boolean[] stack) {
        vis[u] = true;
        stack[u] = true;
        for (Edge e : graph.neighbors(u)) {
            int v = e.to;
            if (!vis[v]) dfs(v, stack);
            else if (stack[v]) cycle = true;
        }
        stack[u] = false;
        order.add(u);
    }
}
