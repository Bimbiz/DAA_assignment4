package utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

public class JSONWriter {
    public static void write(String path, int n, int source, List<int[]> edges) {
        try {
            JSONObject root = new JSONObject();
            root.put("n", n);
            root.put("source", source);
            JSONArray arr = new JSONArray();
            for (int[] e : edges) {
                JSONObject o = new JSONObject();
                o.put("u", e[0]);
                o.put("v", e[1]);
                o.put("w", e[2]);
                arr.put(o);
            }
            root.put("edges", arr);
            try (FileWriter fw = new FileWriter(path)) {
                fw.write(root.toString(2));
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write JSON: " + ex.getMessage(), ex);
        }
    }
}
