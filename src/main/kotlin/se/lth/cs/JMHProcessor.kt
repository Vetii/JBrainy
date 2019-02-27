package se.lth.cs

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.*
import java.lang.Exception

class JMHProcessor {

    fun process(file: File): List<List<String>> {
        return process(FileReader(file))
    }

    fun process(reader : Reader): List<List<String>> {
        var parser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        // We are grouping the parameters by any parameter except the data structure name (which we want)

        val selectedColumns = listOf(
                "Benchmark",
                "Param: seed",
                "Param: applicationSize"
        )

        val seedsToRecords = parser.records.groupBy { record ->
            selectedColumns.map { column -> record.get(column) }
        }

        return seedsToRecords.values.map { records ->
            val interfaceName = records[0].get("Benchmark").let { processBenchmarkName(it) }
            val seed = records[0].get("Param: seed")
            val size = records[0].get("Param: applicationSize")
            // We need to group the runs by data structure size too.
            // We match the size with the higest score found
            val recordsByBaseSize =
                    records.groupBy { it.get("Param: baseStructureSize") }
                            .mapValues { (k, v) -> v.maxBy { it.get("Score") }}
            // We count the number of times the data structure has won
            // (Computing a histogram of the data structure names)
            val bestScoreHist =
                    recordsByBaseSize.values.groupBy { it!!.get("Param: datastructureName") }
                            .mapValues { (k, v) -> v.size}
            val bestScore = bestScoreHist.maxBy { (k, v) -> v }!!.key
            listOf(interfaceName, seed, size, bestScore)
        }
    }

    class JMHProcessorException(override val message: String?) : Exception(message)

    fun processBenchmarkName(benchmark : String) : String {
        val options = listOf("List", "Map", "Set")
        val name = benchmark.findAnyOf(options)?.second
        if (name.isNullOrBlank()) {
            throw JMHProcessorException("Benchmark name does not contain any of $options")
        }
        return name
    }

    /**
     * Prints the given records to a file
     */
    fun print(writer : Writer, records : List<List<String>>) {
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        printer.printRecord("Interface", "Seed", "Size", "Best")
        for (record in records) {
            printer.printRecord(record)
        }
        writer.close()
    }
}
