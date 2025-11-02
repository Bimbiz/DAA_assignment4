package dagsp;

import java.util.*;

public class PathResult {
    private final long[] dist;
    private final int[] parent;
    private final int source;

    public PathResult(long[] dist, int[] parent, int source) {
        this.dist = dist;
        this.parent = parent;
        this.source = source;
    }

    public long[] getDist() { return dist; }
    public int[] getParent() { return parent; }

    public List<Integer> reconstruct(int target) {
        if (target < 0 || target >= parent.length) return Collections.emptyList();
        if (dist[target] == Long.MAX_VALUE / 4 || dist[target] == Long.MIN_VALUE / 4) return Collections.emptyList();
        LinkedList<Integer> path = new LinkedList<>();
        int cur = target;
        while (cur != -1) {
            path.addFirst(cur);
            cur = parent[cur];
        }
        return path;
    }

    public void print() {
        System.out.println("Source: " + source);
        System.out.println("Distances: " + Arrays.toString(dist));
    }
}
