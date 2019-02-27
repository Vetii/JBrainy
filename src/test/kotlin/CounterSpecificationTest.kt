import org.junit.Assert
import org.junit.Test
import papi.Constants
import se.lth.cs.CounterSpecification
import java.io.StringReader

class CounterSpecificationTest {

    /**
     * Test that an empty spec is created with an empty string
     */
    @Test
    fun testEmptySpec() {
        val s = ""
        val result = CounterSpecification.fromReader(StringReader(s))
        Assert.assertTrue(result.currentSpec.isEmpty())
    }

    /**
     * Test that the spec is created with the counter passed in the reader
     */
    @Test
    fun testOneCounterSpec() {
        val s = "PAPI_L1_DCM"
        val result = CounterSpecification.fromReader(StringReader(s))
        Assert.assertEquals(mapOf(Pair("PAPI_L1_DCM", Constants.PAPI_L1_DCM)), result.currentSpec)
    }
}