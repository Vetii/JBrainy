import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import papi.EventSet;
import papi.Papi;
import papi.PapiException;
import se.lth.cs.Application;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;
import se.lth.cs.CounterSpecification;
import se.lth.cs.PapiRunner;
import se.lth.cs.PapiRunnerKt;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
        Map<String, Map<String, List<Long>>> data = runner.runListApplications(10, applications);
        // We check all known Papi counters are in the map

        Assert.assertFalse(data.isEmpty());
        for (String key : data.keySet()) {
            Assert.assertEquals(
                    specification.getCurrentSpec().keySet(),
                    data.get(key).keySet());
            Assert.assertFalse(data.get(key).isEmpty());
        }
    }
}
