package topo;

import java.util.*;

public class TopoResult {
    private final List<Integer> order;

    public TopoResult(List<Integer> order) {
        this.order = new ArrayList<>(order);
    }

    public List<Integer> getOrder() { return Collections.unmodifiableList(order); }

    public void print() {
        System.out.println("Topological order: " + order);
    }
}
