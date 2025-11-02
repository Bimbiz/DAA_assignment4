package metrics;

public interface MetricsInterface {
    void inc(String counter);               // increment 1
    void add(String counter, long value);   // add custom value
    void addTime(String name, long nanos);  // record timing
    long getCount(String counter);
    long getTime(String name);
    void printSummary();                    // print everything
}
