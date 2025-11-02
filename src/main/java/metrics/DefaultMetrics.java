package metrics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;

/**
 * Default implementation of MetricsInterface.
 * Tracks counters and timing values using thread-safe maps.
 */

public class DefaultMetrics implements MetricsInterface {
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> times = new ConcurrentHashMap<>();

    @Override
    public void inc(String counter) {
        counters.computeIfAbsent(counter, k -> new AtomicLong()).incrementAndGet();
    }

    @Override
    public void add(String counter, long value) {
        counters.computeIfAbsent(counter, k -> new AtomicLong()).addAndGet(value);
    }

    @Override
    public void addTime(String name, long nanos) {
        times.computeIfAbsent(name, k -> new AtomicLong()).addAndGet(nanos);
    }

    /** ✅ Returns total recorded nanoseconds for a given timer key. */
    @Override
    public long getTime(String name) {
        return times.getOrDefault(name, new AtomicLong(0)).get();
    }

    @Override
    public long getCount(String counter) {
        return counters.getOrDefault(counter, new AtomicLong(0)).get();
    }

    @Override
    public void printSummary() {
        System.out.println("\n=== Metrics Summary ===");
        if (counters.isEmpty() && times.isEmpty()) {
            System.out.println("(no metrics recorded)");
            return;
        }

        if (!counters.isEmpty()) {
            System.out.println("-- Counters --");
            counters.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        if (!times.isEmpty()) {
            System.out.println("-- Timings (ns) --");
            times.forEach((k, v) -> System.out.println(k + " = " + v + " ns"));
        }
    }
}
