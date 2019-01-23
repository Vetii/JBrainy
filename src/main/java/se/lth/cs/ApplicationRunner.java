package se.lth.cs;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

public class ApplicationRunner {

    /**
     * Creates a list of list applications
     * For each type of list, we create an application
     * @param number
     * @param size
     * @return
     */
    public List<ListApplication> createListApplications(int number, int size) {
        List<ListApplication> listApplications = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            listApplications.add(new ListApplication(i, size, new ArrayList<>()));
            listApplications.add(new ListApplication(i, size, new LinkedList()));
            listApplications.add(new ListApplication(i, size, new Vector<>()));
        }

        return listApplications;
    }

    public List<SetApplication> createSetApplications(int number, int size) {
        List<SetApplication> setApplications = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            setApplications.add(new SetApplication(i, size, new HashSet<>()));
            setApplications.add(new SetApplication(i, size, new TreeSet<>()));
            setApplications.add(new SetApplication(i, size, new LinkedHashSet<>()));
            setApplications.add(new SetApplication(i, size, new ConcurrentSkipListSet<>()));
            setApplications.add(new SetApplication(i, size, new CopyOnWriteArraySet<>()));
        }

        return setApplications;
    }

    public List<MapApplication> createMapApplications(int number, int size) {
        List<MapApplication> mapApplications = new ArrayList<>();

        for (int i = 0; i < number; ++i) {
            mapApplications.add(new MapApplication(i, size, new HashMap<>()));
            mapApplications.add(new MapApplication(i, size, new TreeMap<>()));
            mapApplications.add(new MapApplication(i, size, new IdentityHashMap<>()));
            mapApplications.add(new MapApplication(i, size, new LinkedHashMap<>()));
            mapApplications.add(new MapApplication(i, size, new WeakHashMap<>()));
        }

        return mapApplications;
    }

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
