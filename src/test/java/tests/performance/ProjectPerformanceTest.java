package tests.performance;

import com.sun.management.OperatingSystemMXBean;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jfree.data.xy.XYSeries;
import response.Project;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import static helpers.ApiHelper.deserialize;
import static helpers.ProjectHelper.*;
import static tests.performance.ChartGenerator.createAndSaveChart;
import static tests.performance.PerformanceMetrics.measureTime;
import static tests.performance.PerformanceMetrics.printMetrics;

public class ProjectPerformanceTest {

    private static final int NUM_OBJECTS = 2500;

    public static void main(String[] args) throws Exception {
        XYSeries createTimeSeries = new XYSeries("Create Time for Project");
        XYSeries updateTimeSeries = new XYSeries("Update Time for Project");
        XYSeries deleteTimeSeries = new XYSeries("Delete Time for Project");
        XYSeries createMemoryUsageSeries = new XYSeries("Create Memory Usage for Project");
        XYSeries updateMemoryUsageSeries = new XYSeries("Update Memory Usage for Project");
        XYSeries deleteMemoryUsageSeries = new XYSeries("Delete Memory Usage for Project");
        XYSeries createCpuLoadSeries = new XYSeries("Create CPU Use for Project");
        XYSeries updateCpuLoadSeries = new XYSeries("Update CPU Use for Project");
        XYSeries deleteCpuLoadSeries = new XYSeries("Delete CPU Use for Project");
        XYSeries transactionTimeSeries = new XYSeries("Total Transaction vs Sample Time");
        XYSeries memoryUseSeries = new XYSeries("Total Memory vs Sample Time");
        XYSeries cpuUseSeries = new XYSeries("Total CPU vs Sample Time");

        int object_count = NUM_OBJECTS / 10;
        for (int count = object_count; count <= NUM_OBJECTS; count += object_count) {
            PerformanceMetrics performanceMetrics = performProjectExperiment(count);
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
            printMetrics("Project", count, performanceMetrics);
        }

        createAndSaveChart(createTimeSeries, "Time (ms)", "Create Time vs Number of Objects", "create_time_chart_project.png", "Project");
        createAndSaveChart(updateTimeSeries, "Time (ms)", "Update Time vs Number of Objects", "update_time_chart_project.png", "Project");
        createAndSaveChart(deleteTimeSeries, "Time (ms)", "Delete Time vs Number of Objects", "delete_time_chart_project.png", "Project");
        createAndSaveChart(createMemoryUsageSeries, "Memory (MB)", "Create Memory Usage vs Number of Objects", "create_memory_chart_project.png", "Project");
        createAndSaveChart(updateMemoryUsageSeries, "Memory (MB)", "Update Memory Usage vs Number of Objects", "update_memory_chart_project.png", "Project");
        createAndSaveChart(deleteMemoryUsageSeries, "Memory (MB)", "Delete Memory Usage vs Number of Objects", "delete_memory_chart_project.png", "Project");
        createAndSaveChart(createCpuLoadSeries, "CPU Use (%)", "Create CPU Use vs Number of Objects", "create_cpu_chart_project.png", "Project");
        createAndSaveChart(updateCpuLoadSeries, "CPU Use (%)", "Update CPU Use vs Number of Objects", "update_cpu_chart_project.png", "Project");
        createAndSaveChart(deleteCpuLoadSeries, "CPU Use (%)", "Delete CPU Use vs Number of Objects", "delete_cpu_chart_project.png", "Project");
        createAndSaveChart(transactionTimeSeries, "Sample Time (ms)", "Transaction Time vs Sample Time", "transaction_time_vs_sample_time_chart.png", "Project");
        createAndSaveChart(memoryUseSeries, "Total Memory (MB)", "Memory vs Sample Time", "memory_vs_sample_time_chart.png", "Project");
        createAndSaveChart(cpuUseSeries, "Total CPU (%)", "CPU vs Sample Time", "cpu_vs_sample_time_chart.png", "Project");
    }

    private static PerformanceMetrics performProjectExperiment(int count) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Runtime runtime = Runtime.getRuntime();


        CreateResult createResult = testCreateProjects(httpClient, count);
        List<String> createdProjectIds = createResult.getCreatedIds();

        long sampleStartTime = System.currentTimeMillis();

        // Memory and CPU metrics after creating categories
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();

        long createTime = createResult.getTimeTaken();
        double createMemoryUsage = usedMemory / (1024.0 * 1024.0); // Convert to MB
        double createCpuUsage = osBean.getProcessCpuLoad() * 100; // Convert to percentage

        // Repeat the process for update and delete operations
        long updateTime = measureTime(() -> testUpdateProjects(httpClient, createdProjectIds));

        usedMemory = runtime.totalMemory() - runtime.freeMemory();

        double updateMemoryUsage = (usedMemory) / (1024.0 * 1024.0); // convert to MB
        double updateCpuUsage = osBean.getProcessCpuLoad() * 100;


        long deleteTime = measureTime(() -> testDeleteProjects(httpClient, createdProjectIds));

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

    public static CreateResult testCreateProjects(CloseableHttpClient httpClient, int count) {
        List<String> createdProjectIds = new ArrayList<>();
        long timeTaken = measureTime(() -> {
            for (int i = 0; i < count; i++) {
                try {
                    HttpResponse response = createProject("Project " + i, false, true, "Project Description " + i, httpClient);
                    Project createdProject = deserialize(response, Project.class);
                    createdProjectIds.add(createdProject.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new CreateResult(timeTaken, createdProjectIds);
    }

    private static void testUpdateProjects(CloseableHttpClient httpClient, List<String> createdProjectIds) {
        for (String id : createdProjectIds) {
            try {
                modifyProjectPut(id, "Updated Title for Project " + id, true, false, "Updated Description for Project " + id, httpClient);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void testDeleteProjects(CloseableHttpClient httpClient, List<String> createdProjectIds) {
        for (String id : createdProjectIds) {
            try {
                deleteProject(id, httpClient);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}