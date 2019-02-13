package se.lth.cs;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class MapApplicationBenchmark {

    @State(Scope.Thread)
    public static class MapBenchmarkState {
        @Param({"0", "1", "2", "3"})
        public int seed;

        @Param({"100", "1000"})
        public int applicationSize;

        @Param({"HashMap", "TreeMap", "IdentityHashMap", "LinkedHashMap", "WeakHashMap"})
        public String datastructureName;

        @Param({"0", "1000", "2000"})
        public int baseStructureSize;

        public Application currentApplication;

        @Setup(Level.Trial)
        public void doSetup() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            Map<Integer, Integer> datastructure = (Map<Integer, Integer>) Class.forName("java.util." + datastructureName).newInstance();
            currentApplication = new MapApplication(seed, applicationSize, datastructure);
            currentApplication.populate(baseStructureSize);
        }
    }

    @Benchmark
    public void MapApplicationBenchmark(MapBenchmarkState state, Blackhole blackhole) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        blackhole.consume(state.currentApplication.benchmark());
    }
}
