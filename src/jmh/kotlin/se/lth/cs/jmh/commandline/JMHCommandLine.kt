package se.lth.cs.jmh.commandline

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.runner.options.TimeValue
import se.lth.cs.jmh.JMHTimedRunner
import se.lth.cs.jmh.Main.getAverageError
import se.lth.cs.jmh.Main.getCommit
import java.io.File
import kotlin.system.exitProcess

class JMHCommandLine : CliktCommand() {
    val numberSeeds : Int by option("--seeds", "-s",
            help="The number of seeds to use to generate applications")
            .int()
            .default(100)

    val measurementIterations : Int by option("--measurement-iterations", "-mi",
        help="Number of iterations to measure running time")
            .int()
            .default(10)

    val measurementTime : Long by option("--measurement-time", "-mt",
            help="Duration (in milliseconds) per measurement iteration")
            .long()
            .default(500)

    val warmupIterations : Int by option("--warmup-iterations", "-wi",
            help="Number of iterations to warm up the JVM")
            .int()
            .default(3)

    val warmupTime : Long by option("--warmup-time", "-wt",
            help="Duration (in milliseconds) per warm up iteration")
            .long()
            .default(500)

    val outputFileName : String by option("--output-file", "-o",
            help="File name to store result data")
            .default("jmh-results.csv")

    val infoFileName : String? by option("--info-file", "-p",
            help="File name to write data about JMH run")

    override fun run() {
        val seedsText = IntRange(0, numberSeeds - 1)
                .map { it.toString() }
                .toTypedArray()

        val opts = OptionsBuilder()
                .forks(2)
                .warmupIterations(warmupIterations)
                .warmupTime(TimeValue.milliseconds(warmupTime))
                .measurementTime(TimeValue.milliseconds(measurementTime))
                .measurementIterations(measurementIterations)
                .resultFormat(ResultFormatType.CSV)
                .result(String.format("jmh-results-%s.csv", getCommit()))
                .param("seed", *seedsText)
                .param("baseStructureSize", "0", "1000", "10000")
                .param("applicationSize", "10", "100", "1000")
                .build()

        val r = JMHTimedRunner(opts)
        val results = r.runWithTime()

        if (!infoFileName.isNullOrBlank()) {
            val infofile = File(infoFileName)
            if (infofile.exists()) {
                println("Infofile already exists")
                exitProcess(1)
            }
            infofile.appendText("Running time (s): ${r.duration.seconds}")
            infofile.appendText("\n")
            infofile.appendText("Average error: ${getAverageError(results)}")
        }
    }
}