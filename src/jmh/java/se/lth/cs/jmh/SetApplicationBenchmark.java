package se.lth.cs.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import se.lth.cs.Application;
import se.lth.cs.SetApplication;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SetApplicationBenchmark {

    @State(Scope.Thread)
    public static class SetBenchmarkState {
        @Param({"0", "1", "2", "3"})
        public int seed;

        @Param({"100", "1000"})
        public int applicationSize;

        @Param({"HashSet", "TreeSet", "LinkedHashSet"})
        public String datastructureName;

        @Param({"0", "1000", "2000"})
        public int baseStructureSize;

        public Application currentApplication;

        @Setup(Level.Trial)
        public void doSetup() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            Set<Integer> datastructure = (Set<Integer>) Class.forName("java.util." + datastructureName).newInstance();
            currentApplication = new SetApplication(seed, applicationSize, datastructure);
            currentApplication.populate(baseStructureSize);
        }
    }

    @Benchmark
    public void SetApplicationBenchmark(SetBenchmarkState state, Blackhole blackhole) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        blackhole.consume(state.currentApplication.benchmark());
    }
}
