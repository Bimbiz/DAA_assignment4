package topo;

import graph.DirectedGraph;
import metrics.DefaultMetrics;
import metrics.MetricsInterface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests for Kahn's topological sort algorithm.
 * Checks correct order and cycle detection.
 */

public class KahnTopologicalTest {

    @Test
    public void testSimpleDAGOrder() {
        DirectedGraph g = new DirectedGraph(4);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(1, 3);

        MetricsInterface metrics = new DefaultMetrics();
        KahnTopological k = new KahnTopological(g, metrics);
        TopoResult r = k.run();

        List<Integer> order = r.getOrder();
        // 0 must come before 1, 1 before 2 and 3
        assertTrue(order.indexOf(0) < order.indexOf(1), "0 should come before 1");
        assertTrue(order.indexOf(1) < order.indexOf(2), "1 should come before 2");
        assertTrue(order.indexOf(1) < order.indexOf(3), "1 should come before 3");
    }

    @Test
    public void testCycleDetection() {
        DirectedGraph g = new DirectedGraph(3);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);

        MetricsInterface metrics = new DefaultMetrics();
        KahnTopological k = new KahnTopological(g, metrics);

        assertThrows(IllegalStateException.class, k::run, "Cycle should throw an exception");
    }
}
