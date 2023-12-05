package tests.performance;

import com.sun.management.OperatingSystemMXBean;
import helpers.CategoryHelper;
import helpers.ProjectHelper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jfree.data.xy.XYSeries;
import java.lang.management.ManagementFactory;
import java.util.List;

import static tests.performance.ChartGenerator.createAndSaveChart;
import static tests.performance.PerformanceMetrics.measureTime;
import static tests.performance.PerformanceMetrics.printMetrics;

public class InteroperabilityPerformanceTest {

    private static final int NUM_OBJECTS = 830;

    public static void main(String[] args) throws Exception {
        XYSeries createInteroperabilityTimeSeries = new XYSeries("Create Interoperability Time");
        XYSeries deleteInteroperabilityTimeSeries = new XYSeries("Delete Interoperability Time");
        XYSeries createInteroperabilityMemorySeries = new XYSeries("Create Interoperability Memory Usage");
        XYSeries deleteInteroperabilityMemorySeries = new XYSeries("Delete Interoperability Memory Usage");
        XYSeries createInteroperabilityCPUSeries = new XYSeries("Create Interoperability CPU Use");
        XYSeries deleteInteroperabilityCPUSeries = new XYSeries("Delete Interoperability CPU Use");
        XYSeries transactionTimeSeries = new XYSeries("Total Transaction vs Sample Time");
        XYSeries memoryUseSeries = new XYSeries("Total Memory vs Sample Time");
        XYSeries cpuUseSeries = new XYSeries("Total CPU vs Sample Time");


        int object_count = NUM_OBJECTS / 10;
        for (int count = object_count; count <= NUM_OBJECTS; count += object_count) {
            PerformanceMetrics performanceMetrics = performAssociationExperiment(count);
            createInteroperabilityTimeSeries.add(count, performanceMetrics.getCreateTime());
            deleteInteroperabilityTimeSeries.add(count, performanceMetrics.getDeleteTime());
            createInteroperabilityMemorySeries.add(count, performanceMetrics.getCreateMemoryUsage());
            deleteInteroperabilityMemorySeries.add(count, performanceMetrics.getDeleteMemoryUsage());
            createInteroperabilityCPUSeries.add(count, performanceMetrics.getCreateCpuUsage());
            deleteInteroperabilityCPUSeries.add(count, performanceMetrics.getDeleteCpuUsage());
            transactionTimeSeries.add(performanceMetrics.getSampleTime(), performanceMetrics.getTotalTransactionTime());
            memoryUseSeries.add(performanceMetrics.getSampleTime(), performanceMetrics.getTotalTransactionMemory());
            cpuUseSeries.add(performanceMetrics.getSampleTime(), performanceMetrics.getTotalTransactionCpu());
            printMetrics("Interoperability", count, performanceMetrics);
        }

        createAndSaveChart(createInteroperabilityTimeSeries, "Time (ms)", "Create Time vs Number of Objects", "create_time_chart_interoperability.png", "Interoperability");
        createAndSaveChart(deleteInteroperabilityTimeSeries, "Time (ms)", "Delete Time vs Number of Objects", "delete_time_chart_interoperability.png", "Interoperability");
        createAndSaveChart(createInteroperabilityMemorySeries, "Memory (MB)", "Create Memory Usage vs Number of Objects", "create_memory_chart_interoperability.png", "Interoperability");
        createAndSaveChart(deleteInteroperabilityMemorySeries, "Memory (MB)", "Delete Memory Usage vs Number of Objects", "delete_memory_chart_interoperability.png", "Interoperability");
        createAndSaveChart(createInteroperabilityCPUSeries, "CPU Use (%)", "Create CPU Use vs Number of Objects", "create_cpu_chart_interoperability.png", "Interoperability");
        createAndSaveChart(deleteInteroperabilityCPUSeries, "CPU Use (%)", "Delete CPU Use vs Number of Objects", "delete_cpu_chart_interoperability.png", "Interoperability");
        createAndSaveChart(transactionTimeSeries, "Sample Time (ms)", "Transaction Time vs Sample Time", "transaction_time_vs_sample_time_chart.png", "Interoperability");
        createAndSaveChart(memoryUseSeries, "Total Memory (MB)", "Memory vs Sample Time", "memory_vs_sample_time_chart.png", "Interoperability");
        createAndSaveChart(cpuUseSeries, "Total CPU (%)", "CPU vs Sample Time", "cpu_vs_sample_time_chart.png", "Interoperability");
    }

    private static PerformanceMetrics performAssociationExperiment(int count) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Runtime runtime = Runtime.getRuntime();

        List<String> createdCategoryIds = CategoryPerformanceTest.testCreateCategories(httpClient, count).getCreatedIds();
        List<String> createdProjectIds = ProjectPerformanceTest.testCreateProjects(httpClient, count).getCreatedIds();
        List<String> createdTodoIds = TodoPerformanceTest.testCreateTodos(httpClient, count).getCreatedIds();

        long sampleStartTime = System.currentTimeMillis();

        // Perform association creation and deletion while measuring time, memory, and CPU usage
        long createTime = measureTime(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    CategoryHelper.createAssociation("projects", createdCategoryIds.get(i), createdProjectIds.get(i), httpClient);
                    CategoryHelper.createAssociation("todos", createdCategoryIds.get(i), createdTodoIds.get(i), httpClient);
                    ProjectHelper.createAssociation("tasks", createdProjectIds.get(i), createdTodoIds.get(i), httpClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Memory and CPU metrics after creating categories
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double createMemoryUsage = usedMemory / (1024.0 * 1024.0); // Convert to MB
        double createCpuUsage = osBean.getProcessCpuLoad() * 100; // Convert to percentage

        createTime = createTime / 3;
        createMemoryUsage = createMemoryUsage / 3.0;
        createCpuUsage = createCpuUsage / 3.0;

        // Perform association creation and deletion while measuring time, memory, and CPU usage
        long deleteTime = measureTime(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    CategoryHelper.deleteAssociation("projects", createdCategoryIds.get(i), createdProjectIds.get(i), httpClient);
                    CategoryHelper.deleteAssociation("todos", createdCategoryIds.get(i), createdTodoIds.get(i), httpClient);
                    ProjectHelper.deleteAssociation("tasks", createdProjectIds.get(i), createdTodoIds.get(i), httpClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        usedMemory = runtime.totalMemory() - runtime.freeMemory();
        double deleteMemoryUsage = usedMemory / (1024.0 * 1024.0);
        double deleteCpuUsage = osBean.getProcessCpuLoad() * 100;

        deleteTime = deleteTime / 3;
        deleteMemoryUsage = deleteMemoryUsage / 3.0;
        deleteCpuUsage = deleteCpuUsage / 3.0;

        long sampleEndTime = System.currentTimeMillis();
        long totalTransactionTime = createTime + deleteTime;
        double totalTransactionMemory = createMemoryUsage + deleteMemoryUsage;
        double totalTransactionCpu = createCpuUsage + deleteCpuUsage;
        long sampleTime = sampleEndTime - sampleStartTime;

        httpClient.close();

        PerformanceMetrics performanceMetrics = new PerformanceMetrics();
        performanceMetrics.setCreateTime(createTime);
        performanceMetrics.setDeleteTime(deleteTime);
        performanceMetrics.setCreateMemoryUsage(createMemoryUsage);
        performanceMetrics.setDeleteMemoryUsage(deleteMemoryUsage);
        performanceMetrics.setCreateCpuUsage(createCpuUsage);
        performanceMetrics.setDeleteCpuUsage(deleteCpuUsage);
        performanceMetrics.setTotalTransactionTime(totalTransactionTime);
        performanceMetrics.setTotalTransactionMemory(totalTransactionMemory);
        performanceMetrics.setTotalTransactionCpu(totalTransactionCpu);
        performanceMetrics.setSampleTime(sampleTime);

        return performanceMetrics;
    }
}
