import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import se.lth.cs.JMHProcessor
import java.io.File
import java.io.FileReader
import java.io.StringReader
import java.io.StringWriter

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
        Assert.assertEquals(listOf<String>(), processor!!.process(reader))
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
        val result = processor!!.process(reader)
    Assert.assertEquals(
                listOf(
                        JMHProcessor.JMHRecord(0, 10, 0, "List", "LinkedList", "LinkedList")
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
        val result = processor!!.process(reader)
        Assert.assertEquals(
                listOf(
                    JMHProcessor.JMHRecord(0, 10, 0, "List", "LinkedList", "ArrayList"),
                    JMHProcessor.JMHRecord(0, 10, 0, "List", "ArrayList", "ArrayList")
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
        val result = processor!!.process(reader)
        Assert.assertEquals(
                listOf(
                        JMHProcessor.JMHRecord(0, 10, 0, "List",  "LinkedList", "LinkedList"),
                        JMHProcessor.JMHRecord(0, 10, 0, "Map", "HashMap", "HashMap")
                ),
                result
        )
    }

    @Test
    fun testDifferentBaseSize() {
        val data = generateData()
        val reader = StringReader(data)
        val result = processor!!.process(reader)
        Assert.assertEquals(
                listOf(
                        JMHProcessor.JMHRecord(0, 10,0, "List", "LinkedList", "ArrayList"),
                        JMHProcessor.JMHRecord(0, 10, 0, "List", "ArrayList", "ArrayList"),
                        JMHProcessor.JMHRecord(0, 10, 10, "List", "ArrayList", "ArrayList")
                ),
                result
        )
    }

    @Test
    fun testProcessingFile() {
        val file = File("data/jmh-results-runner-mt=1000.csv")
        Assert.assertTrue(file.exists())
        val fileReader = FileReader(file)
        val result = processor!!.process(fileReader)
        Assert.assertFalse(result.isEmpty())
        // Assert there are no duplicates
        Assert.assertTrue(result.toSet().size == result.size)
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

    @Test
    fun testFileWriting() {
        val file = File("data/jmh-results-runner-mt=1000.csv")
        Assert.assertTrue(file.exists())
        val result = processor!!.process(file)
        val writer = StringWriter()
        processor!!.print(writer, result)
        val parser = CSVParser(StringReader(writer.toString()), CSVFormat.DEFAULT.withFirstRecordAsHeader())

        val numberRegex = Regex("[0-9]+")

        for (record in parser.records) {
            val interfaces = listOf("List", "Map", "Set", "Vector")
            Assert.assertTrue(interfaces.contains(record.get("Interface")))
            Assert.assertTrue(numberRegex.matches(record.get("Seed")))
            Assert.assertTrue(numberRegex.matches(record.get("Size")))
            Assert.assertTrue(numberRegex.matches(record.get("BaseStructureSize")))
            Assert.assertTrue(interfaces.any { record.get("Best").contains(it) })
        }
    }

    companion object {
        /**
         * Static method to generate some example data
         */
        fun generateData() : String {
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
            var row3 =
                    listOf(
                            "\"se.lth.cs.jmh.ListApplicationBenchmark.ListApplicationBenchmark\"",
                            "\"thrpt\"",
                            1,
                            10,
                            414.886418,
                            200.555845,
                            "ops/s",
                            10,
                            10,
                            "ArrayList",
                            0
                    ).joinToString(",")
            data.add(row1)
            data.add(row2)
            data.add(row3)
            val text = "${JMHProcessor.getExpectedHeader()}\n${data.joinToString("\n")}"
            return text
        }
    }
}
