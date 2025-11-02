package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Used after each dataset execution in SCCRunner.
 *
 * CSV format:
 * Dataset,NumComponents,ShortestPathLength,LongestPathLength,Runtime(ns)
 */

public class ResultsLogger {

    private static final String FILE = "results_summary.csv";

    /**
     * Writes one line of results to the CSV file.
     *
     * @param datasetName name of the dataset (e.g., small_1.json)
     * @param numComponents number of SCC components found
     * @param shortestLength total shortest path length (e.g., last node distance)
     * @param longestLength total longest path length
     * @param runtimeNanos total execution time in nanoseconds
     * @param criticalPath list of nodes on the critical path
     */

    public static void logResult(String datasetName, int numComponents, long shortestLength,
                                 long longestLength, long runtimeNanos, List<Integer> criticalPath) {
        try (FileWriter writer = new FileWriter(FILE, true)) {
            // write header if file is new
            if (new java.io.File(FILE).length() == 0) {
                writer.append("Dataset,NumComponents,ShortestPathLength,LongestPathLength,Runtime(ns),CriticalPath\n");
            }

            String pathString = criticalPath.toString().replace(",", " ").replace("[", "").replace("]", "");
            writer.append(datasetName).append(",")
                    .append(String.valueOf(numComponents)).append(",")
                    .append(String.valueOf(shortestLength)).append(",")
                    .append(String.valueOf(longestLength)).append(",")
                    .append(String.valueOf(runtimeNanos)).append(",")
                    .append("\"").append(pathString).append("\"")
                    .append("\n");

            System.out.println("Logged to results_summary.csv");
        } catch (IOException e) {
            System.err.println("Error writing results_summary.csv: " + e.getMessage());
        }
    }
}
