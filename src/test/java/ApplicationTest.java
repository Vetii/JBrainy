import org.junit.Assert;
import org.junit.Test;
import se.lth.cs.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import papi.*;

import static junit.framework.TestCase.assertEquals;

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

        runner.runBenchmarks(runner.createSetApplications(100, 1000));
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

        runner.runBenchmarks(runner.createMapApplications(100, 1000));
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

    @Test(expected = IllegalArgumentException.class)
    public void TestApplicationRunner() throws InvocationTargetException, IllegalAccessException {
        ApplicationRunner appRunner = new ApplicationRunner();

        List<Application> apps = appRunner.createListApplications(100, 1000);
        // We add an application with similar seed but different type !!
        apps.add(new MapApplication(0, 1000, new HashMap<>()));

        List<TrainingSetValue> trainingSet = appRunner.runBenchmarks(
                apps
        );
    }

    @Test
    public void TestPapi() throws PapiException {
        Papi.init();

        // Throws exception
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
}
