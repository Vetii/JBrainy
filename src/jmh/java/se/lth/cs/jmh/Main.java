package se.lth.cs.jmh;

import com.google.gson.Gson;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.api.Git;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Main function to run the jmh benchmakrs and store the results
 */
public class Main {

    public static String getResultKey(RunResult r) {
        return String.format("%s,%s,%s",
                r.getParams().getParam("seed"),
                r.getParams().getParam("applicationSize"),
                r.getParams().getParam("baseStructureSize"));
    }

    public static void process(Collection<RunResult> results) {
        // We group the results by seed
        Map<String, List<RunResult>> resultsBySeed =
                results.stream().collect(
                Collectors.groupingBy(it -> it.getParams().getParam("seed"))
        );

        Map<Integer, String> seedsToDatastructure = new HashMap<>();

        for (String seed : resultsBySeed.keySet()) {
            List<RunResult> rs = resultsBySeed.get(seed);
            // We get the application with the best score
            Optional<RunResult> best = rs.stream().max(
                    Comparator.comparing(it -> it.getAggregatedResult().getPrimaryResult().getScore())
            );
            if (best.isPresent()) {
                seedsToDatastructure.put(Integer.parseInt(seed),
                        best.get().getParams().getParam("datastructureName"));
            } else {
                seedsToDatastructure.put(Integer.parseInt(seed),
                        "unknown");
            }
        }
        System.out.println(new Gson().toJson(seedsToDatastructure));
    }

    public static String getCommit() {
        try {
            FileRepository localRepo = new FileRepository(".git");
            ObjectId o = localRepo.findRef("HEAD").getTarget().getObjectId();
            return ObjectId.toString(o).substring(0, 8);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return "Unable to fetch commit";
    }

    public static void main(String[] args) throws RunnerException, IOException {
        System.out.println("Running the custom jmh runner.");
        // Preparing seeds
        int number_seeds = 20;
        String[] seedsText = new String[number_seeds];
        for (int i = 0; i < number_seeds; ++i)
            seedsText[i] = String.format("%d", i);


        IntStream measurementTimes = IntStream.iterate(250, x -> x <= 1000, x -> x + 250);
        measurementTimes.forEach(
                mt -> {
                    Options opts = new OptionsBuilder()
                            // .include("List")
                            .forks(2)
                            .warmupIterations(3)
                            .warmupTime(TimeValue.milliseconds(250))
                            .measurementTime(TimeValue.milliseconds(mt))
                            .measurementIterations(5)
                            .resultFormat(ResultFormatType.CSV)
                            .result(String.format("jmh-results-%s-runner-mt=%d.csv", getCommit(), mt))
                            .param("seed", seedsText)
                            .param("baseStructureSize", "0", "1000", "10000")
                            .param("applicationSize", "10", "100", "1000")
                            .build();

                    Runner r = new Runner(opts);
                    try {
                        Collection<RunResult> results = r.run();
                    } catch (RunnerException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
