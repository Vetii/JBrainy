import org.junit.Assert;
import org.junit.Test;
import papi.Constants;
import papi.EventSet;
import papi.Papi;
import papi.PapiException;
import se.lth.cs.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import static se.lth.cs.UtilsKt.median;

public class ApplicationTest {

    @Test
    public void TestListApplication() {
        try {
            for (int seed = 0; seed < 100; ++seed) {
                ListApplication arrayListBench = new ListApplication(
                        seed,
                        1000,
                        new ArrayList<>());
                ListApplication linkedListBench = new ListApplication(
                        seed,
                        1000,
                        new LinkedList<>()
                );
                ListApplication vectorBench = new ListApplication(
                        seed,
                        1000,
                        new Vector<>()
                );
                arrayListBench.benchmark();
                linkedListBench.benchmark();
                vectorBench.benchmark();
                Assert.assertEquals(
                        arrayListBench.getDataStructure(),
                        linkedListBench.getDataStructure()
                );
                Assert.assertEquals(
                        arrayListBench.getDataStructure(),
                        vectorBench.getDataStructure()
                );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestListApplication1() throws InvocationTargetException, IllegalAccessException {
        ApplicationRunner runner = new ApplicationRunner();
        runner.runBenchmarks(ApplicationGenerator.createListApplications(100, 1000));
    }

    @Test
    public void TestSetApplication() {
        try {
            for (int seed = 0; seed < 100; ++seed) {
                SetApplication hashSetBench = new SetApplication(seed, 1000, new HashSet<>());

                SetApplication treeSetBench = new SetApplication(seed, 1000, new TreeSet<>());

                hashSetBench.benchmark();
                treeSetBench.benchmark();
                Assert.assertEquals(
                        hashSetBench.getDataStructure(),
                        treeSetBench.getDataStructure()
                );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestSetApplication1() throws InvocationTargetException, IllegalAccessException {
        ApplicationRunner runner = new ApplicationRunner();
        runner.runBenchmarks(ApplicationGenerator.createSetApplications(100, 1000));
    }

    @Test
    public void TestMapApplication() {
        try {
            for (int seed = 0; seed < 100; ++seed) {
                MapApplication hashMapBench = new MapApplication(seed, 1000, new HashMap<>());
                MapApplication hashTableBench = new MapApplication(seed, 1000, new Hashtable<>());
                MapApplication treeMapBench = new MapApplication(seed, 1000, new TreeMap<>());

                hashMapBench.benchmark();
                hashTableBench.benchmark();
                treeMapBench.benchmark();

                Assert.assertEquals(
                        hashMapBench.getDataStructure(),
                        treeMapBench.getDataStructure()
                );

                Assert.assertEquals(
                        hashMapBench.getDataStructure(),
                        hashTableBench.getDataStructure()
                );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestMapApplication1() throws InvocationTargetException, IllegalAccessException {
        ApplicationRunner runner = new ApplicationRunner();
        runner.runBenchmarks(ApplicationGenerator.createMapApplications(100, 1000));
    }

    @Test
    public void TestListApplicationTime() throws InvocationTargetException, IllegalAccessException {
        // Generate 20 applications, run them, get the best (running time, data structure)
        for (int n = 0; n < 20; ++n) {
            List<ListApplication> applications = new ArrayList<>();
            applications.add(new ListApplication(n, 1000, new ArrayList<>()));
            applications.add(new ListApplication(n, 1000, new LinkedList<>()));

            List<Long> runningTimes = new LinkedList<>();

            // Running the benchmark and measuring time
            for (ListApplication app : applications) {
                long startTime = System.nanoTime();
                app.benchmark();
                long endTime = System.nanoTime();
                long duration = (endTime - startTime);
                runningTimes.add(duration);
            }

            for (long t : runningTimes) {
                Assert.assertTrue(t > 0);
            }
        }
    }

    @Test
    public void TestPapi() throws PapiException {
        Papi.init();

        EventSet evset = EventSet.create(Constants.PAPI_TOT_CYC, Constants.PAPI_L1_DCM);

        int[] results = new int[10];

        // 9 warmup runs before measuring
        for (int warmup = 10; warmup >= 0; --warmup) {
            for (int i = 0; i < 10; i++) {
                evset.start();

                // some weird code to measure
                for (int k = 0; k <= i*10; k++) {
                    results[i] += k*k;
                }
                // done with the code

                evset.stop();
                long[] data = evset.getCounters();

                // only print the 10th run
                if (warmup == 0) {
                    System.out.println("#" + i + ":\t" + data[0] + "\t" + data[1]);
                }
            }
        }
    }

    /**
     * Tests that all performance counters can be put in an eventSet
     * (not at the same time!)
     * @throws PapiException
     */
    @Test
    public void TestPapiEventSet() throws PapiException {
        Papi.init();

        int [] constants = se.lth.cs.PapiRunnerKt.getCounters();

        IntPredicate throwsExp =
                (integer -> {
                    try {
                        System.out.println(integer);
                        EventSet evset = EventSet.create(constants[integer],
                                constants[Math.min(integer + 1, 58)]);
                        return false;
                    } catch (PapiException e) {
                        return true;
                    }
                });


        IntStream range = IntStream.range(0, constants.length);
        // IntStream vals = range.filter(throwsExp);
        Assert.assertTrue(range.anyMatch(throwsExp));
    }

    @Test(expected = PapiException.class)
    public void TestBenchmark() throws PapiException {
        PapiRunner r = new PapiRunner();
        r.benchmark();
    }

    @Test
    public void TestEmptyBenchmark() throws PapiException {
        PapiRunner r = new PapiRunner();
        r.emptyBenchmark();
    }

    @Test
    public void TestPapiRunGenerated() throws PapiException {
        List<ListApplication> applications = ApplicationGenerator.createListApplications(
                3,
                100
        );
        PapiRunner papiRunner = new PapiRunner();
        Map<String, List<Long>> data = papiRunner.runListApplications(100, applications);
        Assert.assertFalse(data.isEmpty());
    }

    @Test
    public void TestPapiFeatureGathering() throws PapiException, InvocationTargetException, IllegalAccessException {
        List<ListApplication> applications = ApplicationGenerator.createListApplications(
                3,
                100
        );

        PapiRunner papiRunner = new PapiRunner();
        List<PapiRunner.FeatureVector> data = papiRunner.getFeatures(10, applications);
        System.out.println(data);
    }

    @Test
    public void TestMedian() {
        List<Float> values = new ArrayList<>();
        for (Integer i = 0; i < 10; ++i) {
            values.add(i.floatValue());
        }

        Assert.assertEquals(4.5, median(values), 0.001);

        List<Float> values1 = new ArrayList<>();
        values1.add(1.0f);
        values1.add(4.0f);
        values1.add(5.0f);
        values1.add(3.0f);
        Assert.assertEquals(3.5, median(values1), 0.0001);
    }

    @Test
    public void TestCSVExport() {
        PapiRunner r = new PapiRunner();
        Assert.assertEquals("", r.featuresToCSV(new ArrayList<>()));

        List<PapiRunner.FeatureVector> data = new ArrayList();
        data.add(new PapiRunner.FeatureVector("app1", "java.util.ArrayList",
                new TreeMap<String, Float>() {{ put("COUNTER_1", 123.4f); put("COUNTER_2", 123.4f); }}
                ));
        data.add(new PapiRunner.FeatureVector("app2", "java.util.Vector",
                new TreeMap<String, Float>() {{ put("COUNTER_1", 1234.5f); put("COUNTER_3", 12345f); }}
                ));
        String expectedHeader = "application,data_structure,COUNTER_1,COUNTER_2,COUNTER_3";
        String expectedData1 = "app1,java.util.ArrayList,123.4,123.4,None";
        String expectedData2 = "app2,java.util.Vector,1234.5,None,12345.0";
        Assert.assertEquals(
                expectedHeader + "\n" + expectedData1 + "\n" + expectedData2,
                r.featuresToCSV(data));
    }
}
