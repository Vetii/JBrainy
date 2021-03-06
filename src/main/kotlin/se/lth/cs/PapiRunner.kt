package se.lth.cs

import papi.EventSet
import papi.Papi
import papi.PapiException
import se.lth.cs.ApplicationGeneration.ListApplicationGenerator
import java.io.File
import java.time.Duration
import java.time.Instant
import kotlin.system.exitProcess

open class PapiRunner(counters: CounterSpecification) {
    init {
        Papi.init()
    }

    val counterSpec = counters

    /**
     * Empty benchmark:
     * Test to see if the results are stable.
     */
    fun emptyBenchmark(): MutableMap<String, List<Long>> {
        // For each counter,
        // we store the values for each run (10 runs)
        var data: MutableMap<String, List<Long>> = mutableMapOf()

        for (kvp in counterSpec.currentSpec) {
            val evset = EventSet.create(kvp.value)

            val current: MutableList<Long> = mutableListOf()

            for (warmup in 0..100) {
                val a = (0..warmup).toList().toTypedArray()
                val b = IntArray(warmup)

                evset.start()
                // Synthetic piece of code to see if counters run as expected
                var acc = 0
                for (i in 0 until warmup) {
                    acc += a[i]
                    if (acc % 2 == 1) {
                        b[i] = acc
                    }
                }
                evset.stop()
                // Get data
                val currentData = evset.counters
                current.addAll(currentData.toList())
            }
            data.set(kvp.key, current)
        }
        return data
    }

    /**
     * Runs a function several times
     * @return A map from PAPI counter names to list of values
     */
    inline fun runFunction(numRuns: Int, counter: String, function: () -> Unit): MutableList<Long> {
        val counterId = counterSpec.getCounter(counter)!!
        // We record only one counter
        var evset = EventSet.create()
        try {
            evset = EventSet.create(counterId)
        } catch (e: PapiException) {
            error("Failed to sample counter: ${counter}")
        }

        // We run the function n times
        var values = mutableListOf<Long>()
        for (run in 0..numRuns) {
            // We do the measurements
            evset.start()
            val result = function()
            evset.stop()

            // We record the data
            val data = evset.counters
            values.addAll(data.toList())
        }
        return values
    }


    /** Runs a set of programs (functions) without interleaving
     * (Performance should get better if there is JIT compilation)
     * @Returns A map from couples (counter, program-name) -> values over all runs
     * TODO: Implement this using cartesianProduct from Guava instead
     */
    open fun runApplications(numRuns: Int, applications: List<Application<*>>):
            Map<Application<*>, MutableMap<String, List<Long>>> {

        // We store a map from program names to map with counters and list of values
        var data = mutableMapOf<Application<*>, MutableMap<String, List<Long>>>()
        // We initialize data with empty maps
        for (app in applications) {
            data.put(app, mutableMapOf())
        }

        // For each counter that is available
        for (kvp in counterSpec.currentSpec) {
            val counterName = kvp.key
            println("Streamlined mode: '$counterName'")

            val before = Instant.now()

            // We record only one counter
            val evset = EventSet.create(kvp.value)
            // For each program...
            for (app in applications) {
                data[app]!![counterName] = runApplication(numRuns, evset, app)
            }

            val after = Instant.now()
            val totalDuration = Duration.between(before, after)
            println("Done: ${totalDuration}")
        }

        return data
    }

    private fun runApplication(numRuns: Int, evset: EventSet, app: Application<*>) : MutableList<Long> {
        // We run it n times
        var values = mutableListOf<Long>()
        for (run in 0 until numRuns) {
            // We do the measurements
            evset.start()
            val result = app.benchmark()
            evset.stop()
            app.reset(0)

            //println(result)
            // We record the data
            values.addAll(evset.counters.toList())
        }
        return values
    }

    /**
     * Runs a function several times, gather the performance counters
     * and returns their median
     * @param numRuns Number of times the function should be ran
     * @param function The function to benchmark
     * @return A map from PAPI counter names to the median of their values over numRuns
     */
    fun runFunctionMedian(numRuns: Int, counter: String, function: () -> Unit): Double {
        val data = runFunction(numRuns, counter, function)
        return medianLong(data)
    }

    /**
     * A class for feature vectors with the label of the app (seed), the fastest datastructure for that app,
     * and the performance counters for that app
     */
    data class FeatureVector(val appLabel: String,
                             val dataStructure: String,
                             val bestDataStructure: String,
                             val counters: Map<String, Double>)

    /**
     * Runs a couple of generated applications and returns their feature vectors
     */
    fun getFeatures(numRuns: Int, applications: List<Application<*>>):
            List<FeatureVector> {

        val trainingSet =
                ApplicationRunner().runBenchmarks(applications)

        val distribution = trainingSet.groupBy { it.bestDataStructure }.mapValues { it.value.size }
        println("Benchmark distribution : $distribution")

        val apps = trainingSet.map { it.application }
        val appsTocounters =
                runApplications(numRuns, apps).mapValues {
                    it.value.mapValues { medianLong(it.value) }
                }

        val featureVectors = trainingSet.map {
            FeatureVector(
                    it.application.identifier,
                    it.dataStructure,
                    it.bestDataStructure,
                    appsTocounters.getValue(it.application)
            )
        }
        return featureVectors
    }

    fun featuresToCSV(vectors: List<FeatureVector>): String {
        if (vectors.isEmpty()) return ""

        var header = mutableListOf(
                "application",
                "data_structure",
                "best_data_structure")

        val counters = vectors.map { it.counters.keys }
                .fold(setOf()) { s: Set<String>, v -> s.union(v) }

        header.addAll(counters)
        val headerText = header.joinToString(",")

        var values = mutableListOf<List<String>>()
        for (v in vectors) {
            var l = mutableListOf<String>()
            l.add(v.appLabel)
            l.add(v.dataStructure)
            l.add(v.bestDataStructure)
            for (c in counters) {
                l.add(v.counters[c]?.toString() ?: "None")
            }
            values.add(l)
        }

        val valuesTexts = values.map {
            it.joinToString(",")
        }

        val valuesText = valuesTexts.joinToString("\n")
        return "$headerText\n$valuesText"
    }

    fun processJMHData(numRuns: Int, jmhData: List<JMHProcessor.JMHRecord>): List<FeatureVector> {
        val applications = jmhData.map { processJMHRecord(it)!! }
        val bests = jmhData.map { it.best }

        val appsBest = applications.zip(bests)
                .toMap()

        // Map from application to list of counters
        val results = runApplications(numRuns, applications)
        return results.map {
            val aggregates = it.value.mapValues { medianLong(it.value) }
            val app = it.key
            val best = appsBest[app]!!
            FeatureVector(app.seedString, app.dataStructureName, best, aggregates)
        }
    }

    private fun getClassFromSimpleName(name : String) : Any {
        val className = "java.util.$name"
        return Class.forName(className).getConstructor().newInstance()
    }

    private fun processJMHRecord(record : JMHProcessor.JMHRecord) : Application<*>? {
        var application : Application<*>? = null
        val dataStructure = getClassFromSimpleName(record.datastructure)
        if (record.collection == "List") {
            application = ListApplication(record.seed, record.size, dataStructure as MutableList<Int>?)
            return application
        }

        if (record.collection == "Map") {
            application = MapApplication(record.seed, record.size, dataStructure as MutableMap<Int, Int>?)
            return application
        }

        if (record.collection == "Set") {
            application = SetApplication(record.seed, record.size, dataStructure as MutableSet<Int>?)
            return application
        }
        return null
    }
}
