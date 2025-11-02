package utils;

import graph.DirectedGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.util.*;

public class JSONReader {
    public static DirectedGraph readGraph(String path) {
        try (InputStream is = new FileInputStream(path)) {
            JSONObject root = new JSONObject(new JSONTokener(is));
            int n = root.getInt("n");
            DirectedGraph g = new DirectedGraph(n);
            if (root.has("source")) g.setSource(root.getInt("source"));
            JSONArray edges = root.optJSONArray("edges");
            if (edges != null) {
                for (int i = 0; i < edges.length(); i++) {
                    JSONObject e = edges.getJSONObject(i);
                    int u = e.getInt("u");
                    int v = e.getInt("v");
                    long w = e.has("w") ? e.getLong("w") : 1L;
                    g.addEdge(u, v, w);
                }
            }
            return g;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read JSON graph: " + ex.getMessage(), ex);
        }
    }
}
