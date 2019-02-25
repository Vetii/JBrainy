package se.lth.cs

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.io.FileReader
import java.io.Reader

class JMHProcessor {
    fun processFile(filename: String): List<String?> {
        return processReader(FileReader(File(filename)))
    }

    fun processReader(reader: Reader): List<String?> {
        var parser = CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())
        // We are grouping the parameters by any parameter excep the data structure name (which we want)

        val columns = parser.headerMap.keys
        val selectedColumns = columns
                .filter { it -> it.contains("Param") }
                .minus("Param: datastructureName")

        val seedsToRecords = parser.records.groupBy { record ->
            selectedColumns.map { column -> record.get(column) }
        }

        return seedsToRecords.values.map { records ->
            val seed = records[0].get("Param: seed")
            val size = records[0].get("Param: applicationSize")
            val bestScore = records.maxBy { it.get("Score") }
            return listOf(seed, size, bestScore?.get("Param: datastructureName"))
        }
    }
}
