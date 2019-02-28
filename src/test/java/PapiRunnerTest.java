import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import papi.EventSet;
import papi.Papi;
import papi.PapiException;
import se.lth.cs.*;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class PapiRunnerTest {

    CounterSpecification specification = null;
    PapiRunner runner = null;

    @Before
    public void setup() {
        File papiAvailableCounters = new File("PAPI_FLAGS");
        Assert.assertTrue(papiAvailableCounters.exists());
        specification = CounterSpecification.Companion.fromFile(papiAvailableCounters);
        runner = new PapiRunner(specification);
    }
    /**
     * Tests that all performance counters can be put in an eventSet
     * (not at the same time!)
     *
     * @throws PapiException
     */
    @Test
    public void TestPapiEventSet() throws PapiException {
        Papi.init();

        List<Integer> constants = specification.getCounterValues();

        IntPredicate throwsExp =
                (integer -> {
                    try {
                        System.out.println(integer);
                        EventSet evset = EventSet.create(
                                constants.get(integer),
                                constants.get(Math.min(integer + 1, 58)));
                        return false;
                    } catch (PapiException e) {
                        return true;
                    }
                });


        IntStream range = IntStream.range(0, constants.size());
        // IntStream vals = range.filter(throwsExp);
        Assert.assertTrue(range.anyMatch(throwsExp));
    }

    @Test
    /**
     * Create a list of applications and runs them
     * (The benchmark gives stochastic results, so we only check some data is generated)
     */
    public void TestPapiRunGenerated() throws PapiException {
        List<Application<?>> applications = new ListApplicationGenerator().createApplications(
                0,
                3,
                100
        );
        PapiRunner runner = new PapiRunner(
                CounterSpecification.Companion.fromFile(new File("PAPI_FLAGS"))
        );
        Map<Application<?>, Map<String, List<Long>>> data = runner.runApplications(10, applications);
        // We check all known Papi counters are in the map

        Assert.assertFalse(data.isEmpty());
        for (Application app : data.keySet()) {
            Assert.assertEquals(
                    specification.getCurrentSpec().keySet(),
                    data.get(app).keySet());
            Assert.assertFalse(data.get(app).isEmpty());
        }
    }

    @Test
    public void TestEmptyBenchmark() throws PapiException {
        runner.emptyBenchmark();
    }


    @Test
    public void TestPapiFeatureGathering() throws PapiException, InvocationTargetException, IllegalAccessException {
        List<Application<?>> applications = new ListApplicationGenerator().createApplications(
                0,
                3,
                100
        );

        List<PapiRunner.FeatureVector> data = runner.getFeatures(10, applications);
        Assert.assertFalse(data.isEmpty());
        for (PapiRunner.FeatureVector v : data) {
            Assert.assertFalse(v.getCounters().isEmpty());
        }
    }

    @Test
    public void TestCSVExport() {
        Assert.assertEquals("", runner.featuresToCSV(new ArrayList<>()));

        List<PapiRunner.FeatureVector> data = new ArrayList();
        data.add(new PapiRunner.FeatureVector("app1", "java.util.ArrayList",
                "java.util.ArrayList",
                new TreeMap<String, Double>() {{
                    put("COUNTER_1", 123.4);
                    put("COUNTER_2", 123.4);
                }}
        ));
        data.add(new PapiRunner.FeatureVector("app2", "java.util.Vector",
                "java.util.ArrayList",
                new TreeMap<String, Double>() {{
                    put("COUNTER_1", 1234.5);
                    put("COUNTER_3", 12345.0);
                }}
        ));
        String expectedHeader = "application,data_structure,best_data_structure,COUNTER_1,COUNTER_2,COUNTER_3";
        String expectedData1 = "app1,java.util.ArrayList,java.util.ArrayList,123.4,123.4,None";
        String expectedData2 = "app2,java.util.Vector,java.util.ArrayList,1234.5,None,12345.0";
        Assert.assertEquals(
                expectedHeader + "\n" + expectedData1 + "\n" + expectedData2,
                runner.featuresToCSV(data));
    }

    /**
     * Runs a test from JMH-processed data
     */
    @Test
    public void TestJMHRunner() {
        JMHProcessor processor = new JMHProcessor();
        String data = JMHProcessorTest.Companion.generateData();
        List<JMHProcessor.JMHRecord> processed = processor.process(new StringReader(data));

        List<PapiRunner.FeatureVector> result = runner.processJMHData(processed);

        Assert.assertFalse(result.isEmpty());
    }

}
