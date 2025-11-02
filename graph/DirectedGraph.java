package graph;

import java.util.*;

/**
 * Simple directed graph class.
 * Stores adjacency lists of Edge objects.
 * Supports weighted and unweighted edges.
 */

public class DirectedGraph {
    private final int n;
    private final List<List<Edge>> adj;
    private int source = 0; // default source
    private boolean weighted = false;

    public DirectedGraph(int n) {
        this.n = n;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    //Returns the number of vertices
    public int size() {
        return n;
    }

    //Adds a weighted edge (u -> v) with weight w
    public void addEdge(int u, int v, long w) {
        if (w != 0) weighted = true;
        adj.get(u).add(new Edge(v, w));
    }

    //Adds an unweighted edge (weight = 1)
    public void addEdge(int u, int v) {
        addEdge(u, v, 1L);
    }

    //Returns the adjacency list for a vertex (read-only)
    public List<Edge> neighbors(int u) {
        return Collections.unmodifiableList(adj.get(u));
    }

    //Returns the full adjacency list
    public List<List<Edge>> getAdjList() {
        return adj;
    }

    //Sets the source vertex (for DAG shortest path)
    public void setSource(int s) {
        if (s >= 0 && s < n) source = s;
    }

    //Returns the source vertex
    public int getSource() {
        return source;
    }

    //Returns whether this graph contains any weighted edges
    public boolean isWeighted() {
        return weighted;
    }

    //Counts total edges (for CSV and reporting)
    public int edgeCount() {
        int total = 0;
        for (List<Edge> list : adj) total += list.size();
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DirectedGraph n=" + n + "\n");
        for (int i = 0; i < n; i++) {
            sb.append(i).append(": ");
            for (Edge e : adj.get(i)) sb.append(e).append(" ");
            sb.append("\n");
        }
        return sb.toString();
    }
}
