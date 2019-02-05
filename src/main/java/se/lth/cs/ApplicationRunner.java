package se.lth.cs;

import com.google.gson.Gson;
import se.lth.cs.ApplicationGeneration.ApplicationGenerator;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;
import se.lth.cs.ApplicationGeneration.MapApplicationGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
            List<AppRunData> runningTimes = new ArrayList<>();
            for (Application app : toCompare) {
                runningTimes.add(evaluateApplication(app));
            }

            // We fetch the data corresponding to the fastest
            // application in the list.
            Optional<AppRunData> minimum =
                    runningTimes.stream().min(
                            Comparator.comparingDouble(AppRunData::getMedian)
                    );

            for (AppRunData rt : runningTimes) {
                if (minimum.isPresent()) {
                    AppRunData selected = minimum.get();
                    String bestDataStructure = selected.getApplication().getDataStructureName();

                    trainingSet.add(
                            new TrainingSetValue(rt, rt.getApplication(), bestDataStructure));
                }
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

    public class AppRunData {
        private List<Double> samples;
        private Double average;
        private Double variance;
        private Double median;
        private Application<?> application;
        private Integer numberSamples;

        public AppRunData(Application<?> application, List<Double> samples) {
            this.application = application;
            this.samples = samples;
            this.numberSamples = samples.size();
            this.average = UtilsKt.average(samples);
            this.variance = UtilsKt.variance(samples);
            this.median = UtilsKt.median(samples);
        }

        public List<Double> getSamples() {
            return samples;
        }

        public Double getAverage() {
            return average;
        }

        public Double getVariance() {
            return variance;
        }

        public Double getStandardDeviation() {
            return Math.sqrt(variance);
        }

        public Application<?> getApplication() {
            return application;
        }

        public Integer getNumberSamples() {
            return numberSamples;
        }

        public Double getMedian() {
            return median;
        }

        public List<Double> cleanSamples() {
            List<Double> sorted = samples.stream().sorted().collect(Collectors.toList());
            Boolean even = numberSamples % 2 == 0;
            Double q1, q3, iqr, k;
            int i, j, h, l;
            i = numberSamples / 10;
            h = 9 * numberSamples / 10;
            if (even) {
                j = i + 1;
                l = h + 1;
                q1 = 0.5 * sorted.get(i) + sorted.get(j);
                q3 = 0.5 * sorted.get(h) + sorted.get(l);
            } else {
                q1 = sorted.get(i);
                q3 = sorted.get(h);
            }
            iqr = q3 - q1;
            k = 10.0;
            return samples.stream().filter(
                    x -> x >= (q1) && x <= (q3)
            ).collect(Collectors.toList());
        }

        public List<Double> cleanSamples2() {
            Double up = average + 2 * getStandardDeviation();
            Double down = average - 2 * getStandardDeviation();

            return samples.stream().filter(
                    x -> x >= down && x <= up
            ).collect(Collectors.toList());
        }
    }

    public List<Double> runApplication(Application app) {
        ArrayList<Double> durations = new ArrayList();
        int numberSamples = 1000;

        try {
            for (int i = 0; i < numberSamples; ++i) {
                long startTime = System.nanoTime();
                app.benchmark();
                long endTime = System.nanoTime();
                long duration = (endTime - startTime);
                durations.add((double) duration);
            }
        } catch (Exception ignored) {
            System.out.println("running Application failed");
        }

        return durations;
    }

    public AppRunData evaluateApplication(Application app) {
        List<Double> durations = runApplication(app);

        return new AppRunData(
                app,
                durations
        );
    }

    public List<TrainingSetValue> createListApplicationsSpread(
            Long threshold, int size,
            ApplicationGenerator g) throws InvocationTargetException, IllegalAccessException {

        List<TrainingSetValue> result = new ArrayList<>();
        int seed = 0;
        // We start by creating one application (for all types)
        List<Application<?>> apps = g.createApplications(0, 1, size);
        List<TrainingSetValue> values = runBenchmarks(apps);
        seed += 1;

        Map<String, Long> histogram = values.stream().collect(
                Collectors.groupingBy(TrainingSetValue::getBestDataStructure, Collectors.counting())
        );
        // Fill it with zeros for other applications
        for (Application<?> app : apps) {
            String ds = app.getDataStructure().getClass().getCanonicalName();
            if (!histogram.containsKey(ds)) {
                histogram.put(ds, 0l);
            }
        }

        // While we do not have at least THRESHOLD "worst" applications...
        while (histogram.values().stream().min(Long::compare).orElse(0l) < threshold) {
            // Generate new applications
            apps = g.createApplications(seed, 100, size);
            seed += 100;

            values = runBenchmarks(apps);

            for (TrainingSetValue v : values) {
                String dataStructure = v.getBestDataStructure();
                Long numberApps = histogram.get(dataStructure);
                if (numberApps < threshold * 2) {
                    histogram.put(dataStructure, histogram.get(dataStructure) + 1);
                    result.add(v);
                }
            }
            System.out.println(histogram);
        }

        return result;
    }

    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException {
        // Generate applications
        List<Application<?>> apps = new ListApplicationGenerator().createApplications(0, 50, 200);
        ApplicationRunner r = new ApplicationRunner();

        Gson gson = new Gson();
        // seed, datastructure, best_datastructure, #run, samples, median?, average?, variance?
        ArrayList<ArrayList<String>> CSVData = new ArrayList<>();
        for (int i = 0; i < 20; ++i) {
            List<TrainingSetValue> values = r.runBenchmarks(apps);
            for (TrainingSetValue t : values) {
                ArrayList<String> line = new ArrayList<>();
                line.add(t.getApplication().getSeedString());
                line.add(t.getApplication().getDataStructureName());
                line.add(t.getBestDataStructure());
                line.add(Integer.toString(i));
                for (Double v : t.getRunningData().getSamples()) {
                    line.add(String.format("%.3f", v));
                }
                line.add(String.format("%.3f", t.getRunningData().getAverage()));
                line.add(String.format("%.3f", t.getRunningData().getMedian()));
                line.add(String.format("%.3f", t.getRunningData().getVariance()));
                CSVData.add(line);
            }
        }

        BufferedWriter output = new BufferedWriter(
                new FileWriter("running_times_xbatch.csv")
        );
        for (ArrayList l : CSVData) {
            String s = gson.toJson(l);
            output.write(s.replace("[", "").replace("]", ""));
            output.write("\n");
        }
        output.close();

        /*
        // 1 file for each data structure
        // containing a Map<Seed, List<Double>>
        Map<String, Map<String, List<Double>>> result = new HashMap<>();


        int n = 1;
        for (Application a : apps) {
            System.out.println("App:" + n + "/" + apps.size());
            List<Double> runningTimes = new ArrayList<>(r.evaluateApplication(a).cleanSamples2());

            if (!result.containsKey(a.getDataStructureName())) {
                result.put(a.getDataStructureName(), new HashMap<>());
            }
            result.get(a.getDataStructureName()).put(Integer.toString(a.getSeed()), runningTimes);
            ++n;
        }


        for (String dataStructure : result.keySet()) {
            BufferedWriter output = new BufferedWriter(new FileWriter("runningTimes-" + dataStructure + ".json"));
            String json = gson.toJson(result.get(dataStructure));
            output.write(json);
            output.close();
        }
        */
    }
}
