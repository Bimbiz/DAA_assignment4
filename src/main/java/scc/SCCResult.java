package scc;

import java.util.*;

public class SCCResult {
    private final List<List<Integer>> components;
    private final int[] compId;

    public SCCResult(List<List<Integer>> components, int n) {
        this.components = components;
        this.compId = new int[n];
        Arrays.fill(this.compId, -1);
        for (int cid = 0; cid < components.size(); cid++) {
            for (int v : components.get(cid)) compId[v] = cid;
        }
    }

    public List<List<Integer>> getComponents() { return components; }

    public int getComponentId(int v) { return compId[v]; }

    public int numComponents() { return components.size(); }

    public List<Set<Integer>> buildCondensation(int n, List<List<Integer>> originalAdj) {
        int C = numComponents();
        List<Set<Integer>> cadj = new ArrayList<>(C);
        for (int i = 0; i < C; i++) cadj.add(new HashSet<>());
        for (int u = 0; u < n; u++) {
            for (int v : originalAdj.get(u)) {
                int cu = compId[u], cv = compId[v];
                if (cu != cv) cadj.get(cu).add(cv);
            }
        }
        return cadj;
    }

    public void print() {
        for (int i = 0; i < components.size(); i++) {
            System.out.println("SCC " + i + " (size=" + components.get(i).size() + "): " + components.get(i));
        }
    }
}
