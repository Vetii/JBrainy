import org.junit.Assert;
import org.junit.Test;
import papi.EventSet;
import papi.Papi;
import papi.PapiException;
import se.lth.cs.PapiRunnerKt;

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
}
