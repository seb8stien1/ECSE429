package tests.performance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PerformanceMetrics {
    private long createTime;
    private long updateTime;
    private long deleteTime;
    private double createMemoryUsage;
    private double updateMemoryUsage;
    private double deleteMemoryUsage;
    private double createCpuUsage;
    private double updateCpuUsage;
    private double deleteCpuUsage;

    private long totalTransactionTime;

    private double totalTransactionMemory;

    private double totalTransactionCpu;

    private long sampleTime;

    public static long measureTime(Runnable operation) {
        long startTime = System.currentTimeMillis();
        operation.run();
        return System.currentTimeMillis() - startTime;
    }

    public static void printMetrics(String entityType, int numOfObjects, PerformanceMetrics metrics) {
        String formatHeader = "| %-18s | %-15s | %-15s |%n";
        String formatRow = "| %-18s | %-15.2f | %-15s |%n";
        String formatLong = "| %-18s | %-15d | %-15s |%n";

        // Print the entity type and the header only once
        System.out.println("Entity Type: " + entityType + " | Number of Objects: " + numOfObjects);
        System.out.printf(formatHeader, "Metric", "Value", "Units");
        System.out.println("+--------------------+-----------------+-----------------+");
        System.out.printf(formatLong, "Creation Time", metrics.getCreateTime(), "ms");
        if (!entityType.equals("Interoperability")) System.out.printf(formatLong, "Update Time", metrics.getUpdateTime(), "ms");
        System.out.printf(formatLong, "Deletion Time", metrics.getDeleteTime(), "ms");
        System.out.printf(formatRow, "Creation Memory", metrics.getCreateMemoryUsage(), "MB");
        if (!entityType.equals("Interoperability")) System.out.printf(formatRow, "Update Memory", metrics.getUpdateMemoryUsage(), "MB");
        System.out.printf(formatRow, "Deletion Memory", metrics.getDeleteMemoryUsage(), "MB");
        System.out.printf(formatRow, "Creation CPU", metrics.getCreateCpuUsage(), "%");
        if (!entityType.equals("Interoperability")) System.out.printf(formatRow, "Update CPU", metrics.getUpdateCpuUsage(), "%");
        System.out.printf(formatRow, "Deletion CPU", metrics.getDeleteCpuUsage(), "%");
        System.out.println("+--------------------+-----------------+-----------------+");
    }
}
