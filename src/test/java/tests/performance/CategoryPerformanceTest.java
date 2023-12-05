package tests.performance;

import com.sun.management.OperatingSystemMXBean;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jfree.data.xy.XYSeries;
import response.Category;


import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import static helpers.ApiHelper.deserialize;
import static helpers.CategoryHelper.*;
import static tests.performance.ChartGenerator.createAndSaveChart;
import static tests.performance.PerformanceMetrics.measureTime;
import static tests.performance.PerformanceMetrics.printMetrics;

public class CategoryPerformanceTest {

    private static final int NUM_OBJECTS = 2500;

    public static void main(String[] args) throws Exception {
        XYSeries createTimeSeries = new XYSeries("Create Time for Category");
        XYSeries updateTimeSeries = new XYSeries("Update Time for Category");
        XYSeries deleteTimeSeries = new XYSeries("Delete Time for Category");
        XYSeries createMemoryUsageSeries = new XYSeries("Create Memory Usage for Category");
        XYSeries updateMemoryUsageSeries = new XYSeries("Update Memory Usage for Category");
        XYSeries deleteMemoryUsageSeries = new XYSeries("Delete Memory Usage for Category");
        XYSeries createCpuLoadSeries = new XYSeries("Create CPU Use for Category");
        XYSeries updateCpuLoadSeries = new XYSeries("Update CPU Use for Category");
        XYSeries deleteCpuLoadSeries = new XYSeries("Delete CPU Use for Category");
        XYSeries transactionTimeSeries = new XYSeries("Total Transaction vs Sample Time");
        XYSeries memoryUseSeries = new XYSeries("Total Memory vs Sample Time");
        XYSeries cpuUseSeries = new XYSeries("Total CPU vs Sample Time");

        int object_count = NUM_OBJECTS / 10;
        for (int count = object_count; count <= NUM_OBJECTS; count += object_count) {
            PerformanceMetrics performanceMetrics = performCategoryExperiment(count);
            createTimeSeries.add(count, performanceMetrics.getCreateTime());
            updateTimeSeries.add(count, performanceMetrics.getUpdateTime());
            deleteTimeSeries.add(count, performanceMetrics.getDeleteTime());
            createMemoryUsageSeries.add(count, performanceMetrics.getCreateMemoryUsage());
            updateMemoryUsageSeries.add(count, performanceMetrics.getUpdateMemoryUsage());
            deleteMemoryUsageSeries.add(count, performanceMetrics.getDeleteMemoryUsage());
            createCpuLoadSeries.add(count, performanceMetrics.getCreateCpuUsage());
            updateCpuLoadSeries.add(count, performanceMetrics.getUpdateCpuUsage());
            deleteCpuLoadSeries.add(count, performanceMetrics.getDeleteCpuUsage());
            transactionTimeSeries.add(performanceMetrics.getSampleTime(), performanceMetrics.getTotalTransactionTime());
            memoryUseSeries.add(performanceMetrics.getSampleTime(), performanceMetrics.getTotalTransactionMemory());
            cpuUseSeries.add(performanceMetrics.getSampleTime(), performanceMetrics.getTotalTransactionCpu());
            printMetrics("Category", count, performanceMetrics);
        }

        createAndSaveChart(createTimeSeries, "Time (ms)", "Create Time vs Number of Objects", "create_time_chart_category.png", "Category");
        createAndSaveChart(updateTimeSeries, "Time (ms)", "Update Time vs Number of Objects", "update_time_chart_category.png", "Category");
        createAndSaveChart(deleteTimeSeries, "Time (ms)", "Delete Time vs Number of Objects", "delete_time_chart_category.png", "Category");
        createAndSaveChart(createMemoryUsageSeries, "Memory (MB)", "Create Memory Usage vs Number of Objects", "create_memory_chart_category.png", "Category");
        createAndSaveChart(updateMemoryUsageSeries, "Memory (MB)", "Update Memory Usage vs Number of Objects", "update_memory_chart_category.png", "Category");
        createAndSaveChart(deleteMemoryUsageSeries, "Memory (MB)", "Delete Memory Usage vs Number of Objects", "delete_memory_chart_category.png", "Category");
        createAndSaveChart(createCpuLoadSeries, "CPU Use (%)", "Create CPU Use vs Number of Objects", "create_cpu_chart_category.png", "Category");
        createAndSaveChart(updateCpuLoadSeries, "CPU Use (%)", "Update CPU Use vs Number of Objects", "update_cpu_chart_category.png", "Category");
        createAndSaveChart(deleteCpuLoadSeries, "CPU Use (%)", "Delete CPU Use vs Number of Objects", "delete_cpu_chart_category.png", "Category");
        createAndSaveChart(transactionTimeSeries, "Transaction Time (ms)", "Transaction Time vs Sample Time", "transaction_time_vs_sample_time_chart.png", "Category");
        createAndSaveChart(memoryUseSeries, "Total Memory (MB)", "Memory vs Sample Time", "memory_vs_sample_time_chart.png", "Category");
        createAndSaveChart(cpuUseSeries, "Total CPU (%)", "CPU vs Sample Time", "cpu_vs_sample_time_chart.png", "Category");

    }

    private static PerformanceMetrics performCategoryExperiment(int count) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Runtime runtime = Runtime.getRuntime();

        long sampleStartTime = System.currentTimeMillis();

        CreateResult createResult = testCreateCategories(httpClient, count);
        List<String> createdCategoryIds = createResult.getCreatedIds();

        // Memory and CPU metrics after creating categories
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();

        long createTime = createResult.getTimeTaken();
        double createMemoryUsage = usedMemory / (1024.0 * 1024.0); // Convert to MB
        double createCpuUsage = osBean.getProcessCpuLoad() * 100; // Convert to percentage

        // Repeat the process for update and delete operations
        long updateTime = measureTime(() -> testUpdateCategories(httpClient, createdCategoryIds));

        usedMemory = runtime.totalMemory() - runtime.freeMemory();

        double updateMemoryUsage = (usedMemory) / (1024.0 * 1024.0); // convert to MB
        double updateCpuUsage = osBean.getProcessCpuLoad() * 100;


        long deleteTime = measureTime(() -> testDeleteCategories(httpClient, createdCategoryIds));

        usedMemory = runtime.totalMemory() - runtime.freeMemory();

        double deleteMemoryUsage = (usedMemory) / (1024.0 * 1024.0); // convert to MB
        double deleteCpuUsage = osBean.getProcessCpuLoad() * 100;

        long sampleEndTime = System.currentTimeMillis();
        long totalTransactionTime = createTime + updateTime + deleteTime;
        double totalTransactionMemory = createMemoryUsage + updateMemoryUsage + deleteMemoryUsage;
        double totalTransactionCpu = createCpuUsage + updateCpuUsage + deleteCpuUsage;
        long sampleTime = sampleEndTime - sampleStartTime;

        httpClient.close();

        return new PerformanceMetrics(
                createTime,
                updateTime,
                deleteTime,
                createMemoryUsage,
                updateMemoryUsage,
                deleteMemoryUsage,
                createCpuUsage,
                updateCpuUsage,
                deleteCpuUsage,
                totalTransactionTime,
                totalTransactionMemory,
                totalTransactionCpu,
                sampleTime
        );
    }

    public static CreateResult testCreateCategories(CloseableHttpClient httpClient, int count) {
        List<String> createdCategoryIds = new ArrayList<>();
        long timeTaken = measureTime(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    HttpResponse response = createCategory("Category " + i, "Description " + i, httpClient);
                    Category createdCategory = deserialize(response, Category.class);
                    createdCategoryIds.add(createdCategory.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new CreateResult(timeTaken, createdCategoryIds);
    }

    private static void testUpdateCategories(CloseableHttpClient httpClient, List<String> createdCategoryIds) {
        for (String id : createdCategoryIds) {
            try {
                modifyCategoryPut(id, "Updated Title for Category " + id, "Updated Description for Category " + id, httpClient);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void testDeleteCategories(CloseableHttpClient httpClient, List<String> createdCategoryIds) {
        for (String id : createdCategoryIds) {
            try {
                deleteCategory(id, httpClient);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
