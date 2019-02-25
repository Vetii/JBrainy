import org.junit.Assert
import org.junit.Before
import org.junit.Test
import se.lth.cs.JMHProcessor
import java.io.StringReader

class JMHProcessorTest {

    var processor : JMHProcessor? = null

    var header : String = ""

    @Before
    fun setup() {
        processor = JMHProcessor()

         header =
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
    }

    @Test
    fun TestEmpty() {
        val reader = StringReader("")
        Assert.assertEquals(listOf<String>(), processor!!.processReader(reader))
    }

    @Test
    fun TestOneRow() {
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
                listOf(
                        listOf("0","10","LinkedList")
                )
                , result
        )
    }

    @Test
    fun TestTwoRows() {
        var data = mutableListOf<String>()
        var row1 =
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
        var row2 =
                listOf(
                        "\"se.lth.cs.jmh.ListApplicationBenchmark.ListApplicationBenchmark\"",
                        "\"thrpt\"",
                        1,
                        10,
                        414.886418,
                        200.555845,
                        "ops/s",
                        10,
                        0,
                        "ArrayList",
                        0
                ).joinToString(",")
        data.add(row1)
        data.add(row2)
        val text = "$header\n${data.joinToString("\n")}"
        val reader = StringReader(text)
        val result = processor!!.processReader(reader)
        Assert.assertEquals(
                listOf(
                        listOf("0", "10", "ArrayList")
                ),
                result
        )
    }

    @Test
    fun testDifferentInterface() {
        var data = mutableListOf<String>()
        var row1 =
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
        var row2 =
                listOf(
                        "\"se.lth.cs.jmh.MapApplicationBenchmark.MapApplicationBenchmark\"",
                        "\"thrpt\"",
                        1,
                        10,
                        414.886418,
                        200.555845,
                        "ops/s",
                        10,
                        0,
                        "HashMap",
                        0
                ).joinToString(",")
        data.add(row1)
        data.add(row2)
        val text = "$header\n${data.joinToString("\n")}"
        val reader = StringReader(text)
        val result = processor!!.processReader(reader)
        Assert.assertEquals(
                listOf(
                        listOf("0", "10", "LinkedList"),
                        listOf("0", "10", "HashMap")
                ),
                result
        )
    }

    @Test(expected=JMHProcessor.JMHProcessorException::class)
    fun testBenchmarkProcessingEmpty() {
        Assert.assertEquals("", processor!!.processBenchmarkName(""))
    }
    @Test
    fun testBenchmarkProcessingMap() {
        val bench =
                "\"se.lth.cs.jmh.MapApplicationBenchmark.MapApplicationBenchmark\""
        Assert.assertEquals("Map", processor!!.processBenchmarkName(bench))
    }

    @Test
    fun testBenchmarkProcessingList() {
        val bench =
                "\"se.lth.cs.jmh.ListApplicationBenchmark.ListApplicationBenchmark\""
        Assert.assertEquals("List", processor!!.processBenchmarkName(bench))
    }

    @Test
    fun testBenchmarkProcessingSet() {
        val bench =
                "\"se.lth.cs.jmh.SetApplicationBenchmark.SetApplicationBenchmark\""
        Assert.assertEquals("Set", processor!!.processBenchmarkName(bench))
    }
}
