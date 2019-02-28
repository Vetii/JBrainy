package se.lth.cs.commandline

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import se.lth.cs.CounterSpecification
import se.lth.cs.JMHProcessor
import se.lth.cs.PapiRunner
import java.io.File
import kotlin.system.exitProcess

class PapiCommandLine : CliktCommand() {
    val jmhDataFileName : String by option("-i" , help="The file holding JMH benchmark data")
            .default("jmh-results.csv")

    val papiCountersFileName : String by option("-c", "--couters-file", help="File containing the list of " +
            "PAPI performance counters available")
            .default("papi_avail")

    val outputFileName : String by option("-o", help="The output file for PAPI counter data")
            .default("hardware-perf-data.csv")

    fun checkFileExists(file : File) {
        if (!file.exists()) {
            print("'${file.name}' not found")
            exitProcess(1)
        }
    }

    override fun run() {
        val inputFile = File(jmhDataFileName)
        val outputFile = File(outputFileName)
        val counterSpecFile = File(papiCountersFileName)

        listOf(inputFile, outputFile, counterSpecFile).forEach { checkFileExists(it) }

        val counterSpec = CounterSpecification.fromFile(File("papi_avail"))
        val r = PapiRunner(counterSpec)

        val jmhData = JMHProcessor().process(inputFile)
        val features = r.processJMHData(1, jmhData)
        outputFile.writeText(r.featuresToCSV(features))
    }
}