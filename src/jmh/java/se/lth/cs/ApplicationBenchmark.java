package se.lth.cs;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ApplicationBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        @Param({"0", "1", "2", "3"})
        public int seed;

        @Param({"100", "1000"})
        public int applicationSize;

        @Param({"LinkedList", "ArrayList", "Vector"})
        public String datastructureName;

        public Application currentApplication;

        @Setup(Level.Trial)
        public void doSetup() {
            List<Integer> datastructure;
            switch (datastructureName) {
                case "LinkedList": datastructure = new LinkedList<>(); break;
                case "ArrayList": datastructure = new ArrayList<>(); break;
                case "Vector": datastructure = new Vector<>(); break;
                default: datastructure = new ArrayList<>(); break;
            }
            currentApplication = new ListApplication(seed, applicationSize, datastructure);
        }
    }

    @Benchmark
    public void ListApplicationBenchmark(BenchmarkState state, Blackhole blackhole) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        blackhole.consume(state.currentApplication.benchmark());
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
