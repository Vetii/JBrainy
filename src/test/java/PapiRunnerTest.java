import org.junit.Assert;
import org.junit.Test;
import papi.EventSet;
import papi.Papi;
import papi.PapiException;
import se.lth.cs.Application;
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator;
import se.lth.cs.PapiRunner;
import se.lth.cs.PapiRunnerKt;

import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class PapiRunnerTest {
    /**
     * Tests that all performance counters can be put in an eventSet
     * (not at the same time!)
     *
     * @throws PapiException
     */
    @Test
    public void TestPapiEventSet() throws PapiException {
        Papi.init();

        int[] constants = PapiRunnerKt.getCounters();

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
        PapiRunner papiRunner = new PapiRunner();
        Map<String, Map<String, List<Long>>> data = papiRunner.runListApplications(10, applications);
        // We check all known Papi counters are in the map

        Assert.assertFalse(data.isEmpty());
        for (String key : data.keySet()) {
            Assert.assertEquals(
                    PapiRunnerKt.getCounterSpec().keySet(),
                    data.get(key).keySet());
            Assert.assertFalse(data.get(key).isEmpty());
        }
    }
}
