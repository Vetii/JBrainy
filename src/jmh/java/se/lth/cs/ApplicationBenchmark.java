package se.lth.cs;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class ApplicationBenchmark {

    @Param({"0"})
    public int seed;

    @Param({"1000"})
    public int applicationSize;

    @Benchmark
    public void ListApplicationBenchmark() throws InvocationTargetException, IllegalAccessException {
        Application app = new ListApplication(seed, applicationSize, new ArrayList<>());
        app.benchmark();
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
