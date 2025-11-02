package utils;

import java.util.*;
import java.nio.file.*;

public class DatasetGenerator {
    private static final Random rnd = new Random(42);

    public static void generateAll(String outDir) throws Exception {
        Files.createDirectories(Paths.get(outDir));
        // small: 6-10
        generate(outDir + "/small_1.json", 6, 0.08);
        generate(outDir + "/small_2.json", 8, 0.15, true); // a cycle
        generate(outDir + "/small_3.json", 10, 0.12);

        // medium
        generate(outDir + "/medium_1.json", 12, 0.06);
        generate(outDir + "/medium_2.json", 15, 0.12, true);
        generate(outDir + "/medium_3.json", 18, 0.18);

        // large
        generate(outDir + "/large_1.json", 22, 0.03);
        generate(outDir + "/large_2.json", 30, 0.08, true);
        generate(outDir + "/large_3.json", 40, 0.12);
    }

    private static void generate(String path, int n, double density) throws Exception {
        generate(path, n, density, false);
    }

    private static void generate(String path, int n, double density, boolean ensureCycle) throws Exception {
        int possible = n * (n - 1);
        int m = Math.max(1, (int) (possible * density));
        List<int[]> edges = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        while (edges.size() < m) {
            int u = rnd.nextInt(n), v = rnd.nextInt(n);
            if (u == v) continue;
            long key = ((long)u << 32) | v;
            if (seen.contains(key)) continue;
            seen.add(key);
            edges.add(new int[]{u, v, rnd.nextInt(9) + 1});
        }
        if (ensureCycle) {
            // create a small cycle among first 3 nodes
            if (n >= 3) {
                edges.add(new int[]{0,1,1});
                edges.add(new int[]{1,2,1});
                edges.add(new int[]{2,0,1});
            }
        }
        JSONWriter.write(path, n, 0, edges);
        System.out.println("Wrote " + path + " n=" + n + " m=" + edges.size());
    }

    public static void main(String[] args) throws Exception {
        String out = args.length > 0 ? args[0] : "data";
        generateAll(out);
    }
}
