package se.lth.cs.jmh;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws RunnerException {
        System.out.println("Running the custom jmh runner.");
        String[] seedsText = new String[10];
        for (int i = 0; i < 10; ++i)
            seedsText[i] = String.format("%d", i);

        Options opts = new OptionsBuilder()
                .include("List")
                .forks(1)
                .warmupIterations(3)
                .warmupTime(TimeValue.milliseconds(500))
                .measurementTime(TimeValue.milliseconds(500))
                .measurementIterations(5)
                .resultFormat(ResultFormatType.CSV)
                .result("jmh-results-runner.csv")
                .param("seed", seedsText)
                .build();

        Runner r = new Runner(opts);
        r.run();
    }
}
