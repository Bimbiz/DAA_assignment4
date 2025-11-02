package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * CSVExporter - appends rows of metrics and results into a summary CSV file.
 * It creates folders automatically if they don't exist.
 */
public class CSVExporter {
    private final String path;
    private boolean headerWritten = false;

    public CSVExporter(String path) {
        this.path = path;

        // Ensure directory exists
        File file = new File(path).getParentFile();
        if (file != null && !file.exists()) file.mkdirs();
    }

    /** Writes the header row (only once). */
    public void writeHeader(String... headers) {
        File f = new File(path);
        if (f.exists() && f.length() > 0) return; // already written
        try (FileWriter w = new FileWriter(path, true)) {
            for (int i = 0; i < headers.length; i++) {
                w.append(headers[i]);
                if (i < headers.length - 1) w.append(",");
            }
            w.append("\n");
            headerWritten = true;
        } catch (IOException e) {
            System.err.println("Error writing CSV header: " + e.getMessage());
        }
    }

    /** Appends one row of values to the CSV file. */
    public void writeRow(Object... values) {
        try (FileWriter w = new FileWriter(path, true)) {
            for (int i = 0; i < values.length; i++) {
                w.append(String.valueOf(values[i]));
                if (i < values.length - 1) w.append(",");
            }
            w.append("\n");
        } catch (IOException e) {
            System.err.println("Error writing CSV row: " + e.getMessage());
        }
    }
}
