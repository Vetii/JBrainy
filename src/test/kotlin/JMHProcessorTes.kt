import org.junit.Assert
import org.junit.Before
import org.junit.Test
import se.lth.cs.JMHProcessor
import java.io.StringReader

class JMHProcessorTest {

    var processor : JMHProcessor? = null

    @Before
    fun setup() {
        processor = JMHProcessor()
    }

    @Test
    fun TestEmpty() {
        val reader = StringReader("")
        Assert.assertEquals(listOf<String>(), processor!!.processReader(reader))
    }

    @Test
    fun Test() {
        val header =
                listOf(
                        "Benchmark",
                        "Mode",
                        "Threads",
                        "Samples",
                        "Score",
                        "Score Error (99.9%)",
                        "Unit",
                        "Param: applicationSize",
                        "Param: baseStructureSize",
                        "Param: datastructureName",
                        "Param: seed"
                ).map { "\"$it\""}.joinToString(",")
        val data =
                listOf(
                        "\"se.lth.cs.jmh.ListApplicationBenchmark.ListApplicationBenchmark\"",
                        "\"thrpt\"",
                        1,
                        10,
                        185.524701,
                        29.416447,
                        "ops/s",
                        10,
                        0,
                        "LinkedList",
                        0
                ).joinToString(",")
        val text = "$header\n$data"
        val reader = StringReader(text)
        val result = processor!!.processReader(reader)
        Assert.assertEquals(
                listOf("0","10","LinkedList")
                , result
        )
    }
}
