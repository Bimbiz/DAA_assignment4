package graph;

/**
 * Represents a directed weighted edge (u -> to) in the graph.
 * Immutable once created.
 */

public class Edge {
    //Target vertex
    public final int to;

    //Edge weight or cost
    public final long weight;

    //Creates a new directed edge
    public Edge(int to, long weight) {
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("(%d, w=%d)", to, weight);
    }

    //Optional helper for equals() and hashCode(), useful for testing or sets
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return this.to == other.to && this.weight == other.weight;
    }

    @Override
    public int hashCode() {
        return 31 * to + Long.hashCode(weight);
    }
}
