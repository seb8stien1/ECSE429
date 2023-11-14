package tests.features;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Randomizer {

    public static void main(String[] args) {
        // Path to the resources directory containing the feature files
        String resourcesPath = "src/test/resources";

        // Get a list of all feature files
        List<File> featureFiles = getFeatureFiles(resourcesPath);

        // Randomize the order of the files
        Collections.shuffle(featureFiles);

        // Run each feature file
        for (File file : featureFiles) {
            System.out.println("Running feature file: " + file.getName());
            // runFeatureFile(file.getPath());
        }
    }

    private static List<File> getFeatureFiles(String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".feature"));
        List<File> featureFiles = new ArrayList<>();

        if (files != null) {
            Collections.addAll(featureFiles, files);
        }

        return featureFiles;
    }

    private static void runFeatureFile(String featureFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("mvn", "test", "-Dcucumber.features=" + featureFilePath);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

