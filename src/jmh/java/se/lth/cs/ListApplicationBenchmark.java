package se.lth.cs;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class ListApplicationBenchmark {

    @State(Scope.Thread)
    public static class ListBenchmarkState {

        @Param({"1"})
        public int seed;

        @Param({"1000"})
        public int applicationSize;

        @Param({"LinkedList", "ArrayList", "Vector"})
        public String datastructureName;

        @Param({"0", "1000", "2000"})
        public int baseStructureSize;

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
            currentApplication.populate(baseStructureSize);
        }
    }

    @Benchmark
    public void ListApplicationBenchmark(ListBenchmarkState state, Blackhole blackhole) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        blackhole.consume(state.currentApplication.benchmark());
    }
}
