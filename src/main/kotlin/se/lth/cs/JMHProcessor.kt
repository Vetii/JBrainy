package se.lth.cs

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import java.io.*
import java.lang.Exception

class JMHProcessor {

    fun process(file: File): List<JMHRecord> {
        return process(FileReader(file))
    }

    data class JMHRecord(val seed : Int, val size : Int, val collection : String, val best : String) {
        fun toList() : List<String> {
            return listOf(collection, seed.toString(), size.toString(), best)
        }
    }

    fun process(reader : Reader): List<JMHRecord> {
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
                    .let { Integer.parseInt(it)}
            val size = records[0].get("Param: applicationSize")
                    .let { Integer.parseInt(it)}
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
            // listOf(interfaceName, seed, size, bestScore)
            JMHRecord(seed, size, interfaceName, bestScore)
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
    fun print(writer : Writer, records : List<JMHRecord>) {
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        printer.printRecord("Interface", "Seed", "Size", "Best")
        for (record in records) {
            printer.printRecord(record.toList())
        }
        writer.close()
    }

    companion object {
        /**
         * Static method giving the expected CSV header
         * @return A string for the expected CSV header
         */
        fun getExpectedHeader() : String {
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
            return header
        }
    }
}
