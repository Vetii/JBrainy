import org.junit.Assert
import org.junit.Test
import se.lth.cs.CounterSpecification
import java.io.StringReader

class CounterSpecificationTest {

    @Test
    fun testEmptySpec() {
        val s = ""
        val result = CounterSpecification.fromReader(StringReader(s))
        Assert.assertTrue(result.currentSpec.isEmpty())
    }
}