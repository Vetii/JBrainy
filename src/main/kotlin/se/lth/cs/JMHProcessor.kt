package se.lth.cs

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.lang.Exception

class JMHProcessor {
    fun processFile(filename: String): List<List<String?>> {
        return processReader(FileReader(File(filename)))
    }

    fun processReader(reader: Reader): List<List<String?>> {
        var parser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        // We are grouping the parameters by any parameter excep the data structure name (which we want)

        val columns = parser.headerMap.keys
        // These are the columns we want to use for group by
        val selectedColumns = columns
                .filter { it -> it.contains("Param") }
                .minus("Param: datastructureName")
                .plus("Benchmark")

        val seedsToRecords = parser.records.groupBy { record ->
            selectedColumns.map { column -> record.get(column) }
        }

        return seedsToRecords.values.map { records ->
            val interfaceName = records[0].get("Benchmark").let { processBenchmarkName(it) }
            val seed = records[0].get("Param: seed")
            val size = records[0].get("Param: applicationSize")
            val bestScore = records.maxBy { it.get("Score") }
            listOf(interfaceName, seed, size, bestScore?.get("Param: datastructureName"))
        }
    }

    class JMHProcessorException(override val message: String?) : Exception(message) { }

    fun processBenchmarkName(benchmark : String) : String {
        val options = listOf("List", "Map", "Set")
        val name = benchmark.findAnyOf(options)?.second
        if (name.isNullOrBlank()) {
            throw JMHProcessorException("Benchmark name does not contain any of $options")
        }
        return name
    }
}
