import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.Stopwatch;
import se.lth.cs.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationTest {

    @Test
    public void TestListApplication() {
        try {
            for (int seed = 0; seed < 100; ++seed) {
                ListApplication arrayListBench = new ListApplication(
                        seed,
                        "",
                        new ArrayList<>());
                ListApplication linkedListBench = new ListApplication(
                        seed,
                        "",
                        new LinkedList<>()
                );
                ListApplication vectorBench = new ListApplication(
                        seed,
                        "",
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
                SetApplication hashSetBench = new SetApplication(seed, "", new HashSet<>());

                SetApplication treeSetBench = new SetApplication(seed, "", new TreeSet<>());

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
    public void TestMapApplication() {
        try {
            for (int seed = 0; seed < 100; ++seed) {
                MapApplication hashMapBench = new MapApplication(seed, "", new HashMap<>());
                MapApplication hashTableBench = new MapApplication(seed, "", new Hashtable<>());
                MapApplication treeMapBench = new MapApplication(seed, "", new TreeMap<>());

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
    public void TestListApplicationTime() throws InvocationTargetException, IllegalAccessException {
        // Generate 20 applications, run them, get the best (running time, data structure)
        for (int n = 0; n < 20; ++n) {
            List<ListApplication> applications = new ArrayList<>();
            applications.add(new ListApplication(n, "", new ArrayList<>()));
            applications.add(new ListApplication(n, "", new LinkedList<>()));

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

        List<Application> apps = appRunner.createListApplications();
        // We add an application with similar seed but different type :O
        apps.add(new MapApplication(0, "", new HashMap<>()));

        List<TrainingSetValue> trainingSet = appRunner.runBenchmarks(
                apps
        );
    }
}