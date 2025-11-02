package scc;

import graph.DirectedGraph;
import metrics.DefaultMetrics;
import metrics.MetricsInterface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Tarjan's Strongly Connected Components algorithm.
 * Covers simple graphs with cycles and isolated nodes.
 */

public class TarjanSCCTest {

    @Test
    public void testSingleCycle() {
        DirectedGraph g = new DirectedGraph(3);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);

        MetricsInterface metrics = new DefaultMetrics();
        TarjanSCC t = new TarjanSCC(g, metrics);
        SCCResult r = t.run();

        assertEquals(1, r.numComponents(), "All vertices should be one SCC");
        assertEquals(3, r.getComponents().get(0).size(), "Cycle of 3 nodes should be one SCC of size 3");
    }

    @Test
    public void testTwoSeparateNodes() {
        DirectedGraph g = new DirectedGraph(2);

        MetricsInterface metrics = new DefaultMetrics();
        TarjanSCC t = new TarjanSCC(g, metrics);
        SCCResult r = t.run();

        assertEquals(2, r.numComponents(), "Each isolated node is its own SCC");
    }

    @Test
    public void testChainGraph() {
        DirectedGraph g = new DirectedGraph(4);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);

        MetricsInterface metrics = new DefaultMetrics();
        TarjanSCC t = new TarjanSCC(g, metrics);
        SCCResult r = t.run();

        assertEquals(4, r.numComponents(), "DAG with 4 nodes should have 4 SCCs (no cycles)");
    }
}
