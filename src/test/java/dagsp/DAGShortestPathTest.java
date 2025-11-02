package dagsp;

import graph.DirectedGraph;
import topo.KahnTopological;
import topo.TopoResult;
import metrics.DefaultMetrics;
import metrics.MetricsInterface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple tests for DAGShortestPath.
 * Checks correctness on small graphs.
 */

public class DAGShortestPathTest {

    @Test
    public void testLinearChain() {
        DirectedGraph g = new DirectedGraph(4);
        g.addEdge(0, 1, 2);
        g.addEdge(1, 2, 3);
        g.addEdge(2, 3, 4);
        g.setSource(0);

        MetricsInterface metrics = new DefaultMetrics();

        KahnTopological k = new KahnTopological(g, metrics);
        TopoResult topo = k.run();

        DAGShortestPath sp = new DAGShortestPath(g, metrics);
        PathResult r = sp.run(g.getSource(), topo);

        long[] dist = r.getDist();
        assertEquals(0, dist[0]);
        assertEquals(2, dist[1]);
        assertEquals(5, dist[2]);
        assertEquals(9, dist[3]);
    }

    @Test
    public void testUnreachableNode() {
        DirectedGraph g = new DirectedGraph(3);
        g.addEdge(0, 1, 5);
        // node 2 is disconnected
        g.setSource(0);

        MetricsInterface metrics = new DefaultMetrics();

        KahnTopological k = new KahnTopological(g, metrics);
        TopoResult topo = k.run();

        DAGShortestPath sp = new DAGShortestPath(g, metrics);
        PathResult r = sp.run(0, topo);

        long INF = Long.MAX_VALUE / 4;
        assertEquals(INF, r.getDist()[2], "Unreachable node should stay INF");
    }
}
