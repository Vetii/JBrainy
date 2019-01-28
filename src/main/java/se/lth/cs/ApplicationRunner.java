package se.lth.cs;

import se.lth.cs.ApplicationGeneration.ApplicationGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationRunner {

    /**
     * Runs a list of applications
     * @param applications
     * @return The list of fastest applications
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<TrainingSetValue> runBenchmarks(List<? extends Application> applications)
           throws InvocationTargetException, IllegalAccessException {

        List<TrainingSetValue> trainingSet = new ArrayList<>();

        // We need to group applications by seed.
        // (We are comparing applications with the same seed!)
        Map<Integer, List<Application>> appsBySeed =
                applications.stream().collect(
                        Collectors.groupingBy(Application::getSeed)
                );

        for (Integer seed : appsBySeed.keySet()) {
            // We get the list of applications to compare
            List<Application> toCompare = appsBySeed.get(seed);

            // PRECONDITION:
            // Only similar things should be compared, hence
            // All lists of applications to be compared should be of the same type.
            validateHomogeneity(toCompare);

            // We measure the running time of each application
            // in the list.
            List<TrainingSetValue> runningTimes = new ArrayList<>();
            for (Application app : toCompare) {
                runningTimes.add(runApplication(app));
            }

            // We fetch the data corresponding to the fastest
            // application in the list.
            Optional<TrainingSetValue> minimum =
                    runningTimes.stream().min(
                            Comparator.comparingDouble(TrainingSetValue::getRunningTime)
                    );

            if (minimum.isPresent()) {
                trainingSet.add(minimum.get());
            }
        }

        return trainingSet;
    }

    void validateHomogeneity(List<Application> applications) {
        if (applications.isEmpty()) { return; }

        Application first = applications.get(0);

        for (Application app : applications) {
            if(app.getClass() != first.getClass()) {
                throw new IllegalArgumentException("Applications list should be homogeneous");
            }
        }
    }

    public TrainingSetValue runApplication(Application app)
            throws InvocationTargetException, IllegalAccessException {
        ArrayList<Double> durations = new ArrayList();
        for (int i = 0; i < 100; ++i) {
            long startTime = System.nanoTime();
            app.benchmark();
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            durations.add((double) duration);
        }

        return new TrainingSetValue(UtilsKt.median(durations), app);
    }

    public List<TrainingSetValue> createListApplicationsSpread(
            Long threshold, int size,
            ApplicationGenerator g) throws InvocationTargetException, IllegalAccessException {

        List<TrainingSetValue> result = new ArrayList<>();
        int seed = 0;

        // We start by creating one application
        List<Application<?>> apps = g.createApplications(0, 1, size);
        List<TrainingSetValue> values = runBenchmarks(apps);
        seed += apps.size();

        Map<String, Long> histogram = values.stream().collect(
                Collectors.groupingBy(TrainingSetValue::getDataStructure, Collectors.counting())
        );

        // While we do not have at least THRESHOLD "worst" applications...
        while (histogram.values().stream().min(Long::compare).orElse(0l) < threshold) {
            // Generate new applications
            apps = g.createApplications(seed, 100, size);
            seed += apps.size();
            values = runBenchmarks(apps);

            for (TrainingSetValue v : values) {
                String dataStructure = v.getDataStructure();
                Long numberApps = histogram.get(dataStructure);
                if (numberApps < threshold * 2) {
                    histogram.put(dataStructure, histogram.get(dataStructure) + 1);
                    result.add(v);
                }
            }
        }

        return result;
    }
}
