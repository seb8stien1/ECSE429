package tests.performance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.IOException;

public class ChartGenerator {

    public static void createAndSaveChart(XYSeries series, String yAxisLabel, String chartTitle, String fileName, String directoryPath) throws IOException {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
                "Number of Objects",
                yAxisLabel,
                dataset);

        File chartsDir = new File("src/test/java/tests/performance/graphs/" + directoryPath);

        ChartUtils.saveChartAsPNG(new File(chartsDir, fileName), chart, 800, 600);
    }
}
