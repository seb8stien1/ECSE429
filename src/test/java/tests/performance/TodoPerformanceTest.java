package tests.performance;

import com.sun.management.OperatingSystemMXBean;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jfree.data.xy.XYSeries;
import response.Todo;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import static helpers.ApiHelper.deserialize;
import static helpers.TodoHelper.*;
import static tests.performance.ChartGenerator.createAndSaveChart;
import static tests.performance.PerformanceMetrics.measureTime;
import static tests.performance.PerformanceMetrics.printMetrics;

public class TodoPerformanceTest {

    private static final int NUM_OBJECTS = 2500;

    public static void main(String[] args) throws Exception {
        XYSeries createTimeSeries = new XYSeries("Create Time for Todo");
        XYSeries updateTimeSeries = new XYSeries("Update Time for Todo");
        XYSeries deleteTimeSeries = new XYSeries("Delete Time for Todo");
        XYSeries createMemoryUsageSeries = new XYSeries("Create Memory Usage for Todo");
        XYSeries updateMemoryUsageSeries = new XYSeries("Update Memory Usage for Todo");
        XYSeries deleteMemoryUsageSeries = new XYSeries("Delete Memory Usage for Todo");
        XYSeries createCpuLoadSeries = new XYSeries("Create CPU Use for Todo");
        XYSeries updateCpuLoadSeries = new XYSeries("Update CPU Use for Todo");
        XYSeries deleteCpuLoadSeries = new XYSeries("Delete CPU Use for Todo");
        XYSeries transactionTimeSeries = new XYSeries("Total Transaction vs Sample Time");
        XYSeries memoryUseSeries = new XYSeries("Total Memory vs Sample Time");
        XYSeries cpuUseSeries = new XYSeries("Total CPU vs Sample Time");

        int object_count = NUM_OBJECTS / 10;
        for (int count = object_count; count <= NUM_OBJECTS; count += object_count) {
            PerformanceMetrics performanceMetrics = performTodoExperiment(count);
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
            printMetrics("Todo", count, performanceMetrics);
        }

        createAndSaveChart(createTimeSeries, "Time (ms)", "Create Time vs Number of Objects", "create_time_chart_todo.png", "Todo");
        createAndSaveChart(updateTimeSeries, "Time (ms)", "Update Time vs Number of Objects", "update_time_chart_todo.png", "Todo");
        createAndSaveChart(deleteTimeSeries, "Time (ms)", "Delete Time vs Number of Objects", "delete_time_chart_todo.png", "Todo");
        createAndSaveChart(createMemoryUsageSeries, "Memory (MB)", "Create Memory Usage vs Number of Objects", "create_memory_chart_todo.png", "Todo");
        createAndSaveChart(updateMemoryUsageSeries, "Memory (MB)", "Update Memory Usage vs Number of Objects", "update_memory_chart_todo.png", "Todo");
        createAndSaveChart(deleteMemoryUsageSeries, "Memory (MB)", "Delete Memory Usage vs Number of Objects", "delete_memory_chart_todo.png", "Todo");
        createAndSaveChart(createCpuLoadSeries, "CPU Use (%)", "Create CPU Use vs Number of Objects", "create_cpu_chart_todo.png", "Todo");
        createAndSaveChart(updateCpuLoadSeries, "CPU Use (%)", "Update CPU Use vs Number of Objects", "update_cpu_chart_todo.png", "Todo");
        createAndSaveChart(deleteCpuLoadSeries, "CPU Use (%)", "Delete CPU Use vs Number of Objects", "delete_cpu_chart_todo.png", "Todo");
        createAndSaveChart(transactionTimeSeries, "Sample Time (ms)", "Transaction Time vs Sample Time", "transaction_time_vs_sample_time_chart.png", "Todo");
        createAndSaveChart(memoryUseSeries, "Total Memory (MB)", "Memory vs Sample Time", "memory_vs_sample_time_chart.png", "Todo");
        createAndSaveChart(cpuUseSeries, "Total CPU (%)", "CPU vs Sample Time", "cpu_vs_sample_time_chart.png", "Todo");
    }

    private static PerformanceMetrics performTodoExperiment(int count) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Runtime runtime = Runtime.getRuntime();

        long sampleStartTime = System.currentTimeMillis();

        CreateResult createResult = testCreateTodos(httpClient, count);
        List<String> createdTodoIds = createResult.getCreatedIds();

        // Memory and CPU metrics after creating categories
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();

        long createTime = createResult.getTimeTaken();
        double createMemoryUsage = usedMemory / (1024.0 * 1024.0); // Convert to MB
        double createCpuUsage = osBean.getProcessCpuLoad() * 100; // Convert to percentage

        // Repeat the process for update and delete operations
        long updateTime = measureTime(() -> testUpdateTodos(httpClient, createdTodoIds));

        usedMemory = runtime.totalMemory() - runtime.freeMemory();

        double updateMemoryUsage = (usedMemory) / (1024.0 * 1024.0); // convert to MB
        double updateCpuUsage = osBean.getProcessCpuLoad() * 100;


        long deleteTime = measureTime(() -> testDeleteTodos(httpClient, createdTodoIds));

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

    public static CreateResult testCreateTodos(CloseableHttpClient httpClient, int count) {
        List<String> createdTodoIds = new ArrayList<>();
        long timeTaken = measureTime(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    HttpResponse response = createTodo("Todo " + i, false, "Todo Description " + i, httpClient);
                    Todo createdTodo = deserialize(response, Todo.class);
                    createdTodoIds.add(createdTodo.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new CreateResult(timeTaken, createdTodoIds);
    }

    private static void testUpdateTodos(CloseableHttpClient httpClient, List<String> createdTodoIds) {
        for (String id : createdTodoIds) {
            try {
                modifyTodoPut(id, "Updated Title for Todo " + id, true, "Updated Description for Todo " + id, httpClient);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void testDeleteTodos(CloseableHttpClient httpClient, List<String> createdTodoIds) {
        for (String id : createdTodoIds) {
            try {
                deleteTodo(id, httpClient);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
