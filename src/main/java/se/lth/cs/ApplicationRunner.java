package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
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
                            (x, y) -> Long.compare(x.getRunningTime(), y.getRunningTime())
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

    TrainingSetValue runApplication(Application app)
            throws InvocationTargetException, IllegalAccessException {
        long startTime = System.nanoTime();
        app.benchmark();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        return new TrainingSetValue(duration, app);
    }
}
