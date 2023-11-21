package tests.performance;


public class PerformanceTestRunner {
    public static void main(String[] args) throws Exception {
        CategoryPerformanceTest.main(args);
        TodoPerformanceTest.main(args);
        ProjectPerformanceTest.main(args);
        InteroperabilityPerformanceTest.main(args);
    }
}
