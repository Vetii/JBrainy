package se.lth.cs;

import org.openjdk.jmh.annotations.*;
import se.lth.cs.ApplicationGeneration.ApplicationGenerator;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        @Param({"0"})
        public int seed;

        @Param({"1000"})
        public int applicationSize;

        private List<Application> applications;
        {
            ApplicationRunner runner = new ApplicationRunner();
            try {
                List<TrainingSetValue> phase1Set = runner.runBenchmarks(
                        new ListApplicationGenerator().createApplications(0, 2, 10000)
                );
                applications = phase1Set.stream().map(TrainingSetValue::getApplication).collect(Collectors.toList());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public Application application;

        @Setup(Level.Trial)
        public void doSetup() {
            application = applications.get(seed);
            // application = new ListApplication(seed, applicationSize, new ArrayList<>());
        }
    }

    @Benchmark
    public void ListApplicationBenchmark(BenchmarkState state) throws InvocationTargetException, IllegalAccessException {
        state.application.benchmark();
    }

    // public static void main(String[] args) throws RunnerException, IOException {
    //     ApplicationRunner runner = new ApplicationRunner();
    //     List<TrainingSetValue> phase1Set = new ArrayList<>();
    //     try {
    //         phase1Set = runner.runBenchmarks(
    //                 runner.createListApplications(2, 1000)
    //         );
    //     } catch (InvocationTargetException e) {
    //         e.printStackTrace();
    //     } catch (IllegalAccessException e) {
    //         e.printStackTrace();
    //     }

    //     if (phase1Set.isEmpty()) { return; }

    //     List<String> seeds =
    //             phase1Set.stream().map((v) ->
    //                 Integer.toString(v.getApplication().seed)).collect(Collectors.toList());

    //     List<String> applicationSizes =
    //             phase1Set.stream().map((v) ->
    //                 Integer.toString(v.getApplication().applicationSize))
    //             .collect(Collectors.toList());

    //     Options opt = new OptionsBuilder()
    //             .include(ApplicationBenchmark.class.getSimpleName())
    //             .param("seed", seeds.toArray(new String[0]))
    //             .param("applicationSize", applicationSizes.toArray(new String[0]))
    //             .build();
    //     new Runner(opt).run();
    // }
}
