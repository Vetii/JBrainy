package se.lth.cs

import papi.Constants
import papi.EventSet
import papi.Papi
import papi.PapiException
import se.lth.cs.ApplicationGeneration.MapApplicationGenerator
import java.io.File
import java.util.*

val counterSpec =
            hashMapOf(
                    "PAPI_L1_DCM" to Constants.PAPI_L1_DCM,
                    "PAPI_L1_ICM" to Constants.PAPI_L1_ICM,
                    "PAPI_L2_DCM" to Constants.PAPI_L2_DCM,
                    "PAPI_L2_ICM" to Constants.PAPI_L2_ICM,
                    "PAPI_L1_TCM" to Constants.PAPI_L1_TCM,
                    "PAPI_L2_TCM" to Constants.PAPI_L2_TCM,
                    "PAPI_L3_TCM" to Constants.PAPI_L3_TCM,
                    "PAPI_CA_SNP" to Constants.PAPI_CA_SNP,
                    "PAPI_CA_SHR" to Constants.PAPI_CA_SHR,
                    "PAPI_CA_CLN" to Constants.PAPI_CA_CLN,
                    "PAPI_CA_ITV" to Constants.PAPI_CA_ITV,
                    "PAPI_L3_LDM" to Constants.PAPI_L3_LDM,
                    "PAPI_TLB_DM" to Constants.PAPI_TLB_DM,
                    "PAPI_TLB_IM" to Constants.PAPI_TLB_IM,
                    "PAPI_L1_LDM" to Constants.PAPI_L1_LDM,
                    "PAPI_L1_STM" to Constants.PAPI_L1_STM,
                    "PAPI_L2_LDM" to Constants.PAPI_L2_LDM,
                    "PAPI_L2_STM" to Constants.PAPI_L2_STM,
                    "PAPI_PRF_DM" to Constants.PAPI_PRF_DM,
                    "PAPI_MEM_WCY" to Constants.PAPI_MEM_WCY,
                    "PAPI_STL_ICY" to Constants.PAPI_STL_ICY,
                    "PAPI_FUL_ICY" to Constants.PAPI_FUL_ICY,
                    "PAPI_STL_CCY" to Constants.PAPI_STL_CCY,
                    "PAPI_FUL_CCY" to Constants.PAPI_FUL_CCY,
                    "PAPI_BR_UCN" to Constants.PAPI_BR_UCN,
                    "PAPI_BR_CN" to Constants.PAPI_BR_CN,
                    "PAPI_BR_TKN" to Constants.PAPI_BR_TKN,
                    "PAPI_BR_NTK" to Constants.PAPI_BR_NTK,
                    "PAPI_BR_MSP" to Constants.PAPI_BR_MSP,
                    "PAPI_BR_PRC" to Constants.PAPI_BR_PRC,
                    "PAPI_TOT_INS" to Constants.PAPI_TOT_INS,
                    "PAPI_LD_INS" to Constants.PAPI_LD_INS,
                    "PAPI_SR_INS" to Constants.PAPI_SR_INS,
                    "PAPI_BR_INS" to Constants.PAPI_BR_INS,
                    "PAPI_RES_STL" to Constants.PAPI_RES_STL,
                    "PAPI_TOT_CYC" to Constants.PAPI_TOT_CYC,
                    "PAPI_LST_INS" to Constants.PAPI_LST_INS,
                    "PAPI_L2_DCA" to Constants.PAPI_L2_DCA,
                    "PAPI_L3_DCA" to Constants.PAPI_L3_DCA,
                    "PAPI_L2_DCR" to Constants.PAPI_L2_DCR,
                    "PAPI_L3_DCR" to Constants.PAPI_L3_DCR,
                    "PAPI_L2_DCW" to Constants.PAPI_L2_DCW,
                    "PAPI_L3_DCW" to Constants.PAPI_L3_DCW,
                    "PAPI_L2_ICH" to Constants.PAPI_L2_ICH,
                    "PAPI_L2_ICA" to Constants.PAPI_L2_ICA,
                    "PAPI_L3_ICA" to Constants.PAPI_L3_ICA,
                    "PAPI_L2_ICR" to Constants.PAPI_L2_ICR,
                    "PAPI_L3_ICR" to Constants.PAPI_L3_ICR,
                    "PAPI_L2_TCA" to Constants.PAPI_L2_TCA,
                    "PAPI_L3_TCA" to Constants.PAPI_L3_TCA,
                    "PAPI_L2_TCR" to Constants.PAPI_L2_TCR,
                    "PAPI_L3_TCR" to Constants.PAPI_L3_TCR,
                    "PAPI_L2_TCW" to Constants.PAPI_L2_TCW,
                    "PAPI_L3_TCW" to Constants.PAPI_L3_TCW,
                    "PAPI_SP_OPS" to Constants.PAPI_SP_OPS,
                    "PAPI_DP_OPS" to Constants.PAPI_DP_OPS,
                    "PAPI_VEC_SP" to Constants.PAPI_VEC_SP,
                    "PAPI_VEC_DP" to Constants.PAPI_VEC_DP,
                    "PAPI_REF_CYC" to Constants.PAPI_REF_CYC
            )

    val counters = counterSpec.values.toIntArray()

class PapiRunner() {
    init {
        Papi.init()
    }
    /**
     * Empty benchmark:
     * Test to see if the results are stable.
     */
    fun emptyBenchmark(): MutableMap<String, List<Long>> {
        // For each counter,
        // we store the values for each run (10 runs)
        var data : MutableMap<String, List<Long>> = mutableMapOf()

        for (kvp in counterSpec) {
            val evset = EventSet.create(kvp.value)

            val current : MutableList<Long> = mutableListOf()

            for(warmup in 0 .. 100) {
                val a = (0..warmup).toList().toTypedArray()
                val b = IntArray(warmup)

                evset.start()
                // Synthetic piece of code to see if counters run as expected
                var acc = 0
                for (i in 0 until warmup) {
                    acc += a[i]
                    if(acc % 2 == 1) {
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

    fun benchmark() {
        // Throws exception
        val evset = EventSet.create(*counters)

        val results = IntArray(10)

        // 9 warmup runs before measuring
        for (warmup in 10 downTo 0) {
            for (i in 0..9) {
                evset.start()

                // some weird code to measure
                for (k in 0..i * 10) {
                    results[i] += k * k
                }
                // done with the code

                evset.stop()
                val data = evset.counters

                // only print the 10th run
                if (warmup == 0) {
                    println("#" + i + ":\t" + data[0] + "\t" + data[1])
                }
            }
        }
    }


    class CounterAndProgram(val counter : String, val programName : String) : Comparable<CounterAndProgram> {
        override fun toString(): String { return "${counter}_$programName"}
        override fun compareTo(other: CounterAndProgram): Int {
            return toString().compareTo(other.toString())
        }
    }

    /** Runs a set of programs (functions) without interleaving
     * (Performance should get better if there is JIT compilation)
     * Known bug: Crashes if there are too many functions (creates even sets too many times)
     * @Returns A map from couples counter_program-name -> List<Long> over all runs
     */
    fun runWithoutInterleaving(numRuns : Int, functions : List<Pair<String,() -> Any>>):
            MutableMap<CounterAndProgram, List<Long>> {
        var data : MutableMap<CounterAndProgram, List<Long>> = mutableMapOf()
        for (labelAndFunction in functions) {
            val function = labelAndFunction.second
            val label = labelAndFunction.first
            val values = runFunction(numRuns, function)
            for (counterAndValues in values) {
                data.put(CounterAndProgram(counterAndValues.key, label),
                        counterAndValues.value)
            }
        }
        return data
    }

    /**
     * Runs a function several times
     * @return A map from PAPI counter names to list of values
     */
    inline fun runFunction(numRuns : Int, function : () -> Any): SortedMap<String, List<Long>> {
        var data : MutableMap<String, List<Long>> = mutableMapOf()

        for (kvp in counterSpec) {
            // We record only one counter
            var evset = EventSet.create()
            try {
                evset = EventSet.create(kvp.value)
            } catch (e : PapiException) {
                error("Failed to sample counter: ${kvp.key}")
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
            data[kvp.key] = values
        }
        return data.toSortedMap()
    }


    /** Runs a set of programs (functions) without interleaving
     * (Performance should get better if there is JIT compilation)
     * @Returns A map from couples (counter, program-name) -> values over all runs
     */
    inline fun runWithoutInterleaving2(numRuns : Int, functions : List<Pair<String,() -> Any>>):
            SortedMap<CounterAndProgram, List<Long>> {

        var data = TreeMap<CounterAndProgram, List<Long>>()

        // For each counter that is available
        for (kvp in counterSpec) {
            println("Streamlined mode: " + "'" + kvp.key + "'")
            // We record only one counter
            val evset = EventSet.create(kvp.value)
            // For each program...
            for (function in functions) {
                val current = Pair(kvp.key, function.first)
                // We run it n times
                var values = mutableListOf<Long>()
                for (run in 0 until numRuns) {
                    // We do the measurements
                    evset.start()
                    val result = function.second()
                    evset.stop()

                    //println(result)
                    // We record the data
                    val data = evset.counters
                    values.addAll(data.toList())
                }
                val key = CounterAndProgram(current.first, current.second)
                data[key] = values
            }
        }

        return data
    }

    /**
     * Runs a function several times, gather the performance counters
     * and returns their median
     * @param numRuns Number of times the function should be ran
     * @param function The function to benchmark
     * @return A map from PAPI counter names to the median of their values over numRuns
     */
    fun runFunctionMedian(numRuns : Int, function : () -> Any) : SortedMap<String, Double> {
        val data = runFunction(numRuns, function)
        return data.mapValues {
            median(it.value.map { it.toDouble() })
        }.toSortedMap()
    }

    /**
     * Runs a list of generated applications without interleaving
     * @Returns A map from couple counter_program-name -> values over all runs
     */
    fun runListApplications(numRuns: Int, applications: List<Application<*>>):
            Map<CounterAndProgram, List<Long>> {
        val apps = applications.map {
            Pair("${it.seed}:${it.dataStructure.javaClass.canonicalName}", { it.benchmark() })
        }

        return runWithoutInterleaving2(numRuns, apps).toMap()
    }

    /**
     * A class for feature vectors with the label of the app (seed), the fastest datastructure for that app,
     * and the performance counters for that app
     */
    data class FeatureVector(val appLabel : String, val dataStructure : String, val counters : SortedMap<String, Double>)

    /**
     * Runs a couple of generated applications and returns their feature vectors
     */
    fun getFeatures(numRuns: Int, applications : List<Application<*>>):
            List<FeatureVector> {
        val trainingSet =
                ApplicationRunner().runBenchmarks(applications)

        val distribution = trainingSet.groupBy { it.dataStructure }.mapValues { it.value.size }
        println("Benchmark distribution : $distribution")

        val apps = trainingSet.map { it.application }
        val counters = runListApplications(numRuns, apps)


        // Map program_name -> { counters -> values }
        var results : MutableMap<String, SortedMap<String, Double>> = mutableMapOf()
        for (kvp in counters) {
            val progName = kvp.key.programName
            if (results.containsKey(progName)) {
                results[progName]!![kvp.key.counter] = medianLong(kvp.value)
            } else {
                results[progName] = sortedMapOf()
                results[progName]!![kvp.key.counter] = medianLong(kvp.value)
            }
        }

        val l = results.map {
            FeatureVector(
                    it.key,
                    it.key.split(":")[1], // Data structure
                    it.value
            )
        }
        return l
    }

    fun featuresToCSV(vectors : List<FeatureVector>) : String {
        if (vectors.isEmpty()) return ""

        var header = mutableListOf(
                "application",
                "data_structure")

        val counters = vectors.map { it.counters.keys }
                .fold(setOf()) { s : Set<String>, v -> s.union(v)}

        header.addAll(counters)
        val headerText = header.joinToString(",")

        var values = mutableListOf<List<String>>()
        for (v in vectors) {
            var l = mutableListOf<String>()
            l.add(v.appLabel)
            l.add(v.dataStructure)
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

    data class BenchmarkId(val counter : String, val program : String)

    inline fun runWithInterleaving(numRuns : Int, functions : List<Pair<String, () -> Any>>):
            Map<String, List<Long>> {
        var data : MutableMap<BenchmarkId, MutableList<Long>> = mutableMapOf()

        for (kvp in counterSpec) {
            val evset = EventSet.create(kvp.value)

            println("Interleaved mode: " + "'" + kvp.key + "'")
            // For each run-number
            for (run in 0..numRuns) {
                // We run each program
                for (function in functions) {
                    val current = BenchmarkId(kvp.key, function.first)
                    if (!data.containsKey(current)) {
                        data[current] = mutableListOf()
                    }
                    // We do the measurements
                    evset.start()
                    val result = function.second()
                    evset.stop()

                    //println(result)
                    // We record the data
                    val counterdata = evset.counters
                    data[current]?.addAll(counterdata.toList())
                }
            }
        }

        return data.mapKeys { current ->
            current.key.counter + "_" + current.key.program }
                .mapValues { l -> l.value.toList() }
    }
}

fun main(args : Array<String>) {
    val r = PapiRunner()
    val apps = MapApplicationGenerator().createApplications(200, 100, 100)
    val data = r.getFeatures(200, apps)
    val file = File("benchmarkoutput.csv")
    file.writeText(r.featuresToCSV(data))

    /*
    val gson = Gson()
    val data1 = r.runListApplications(100, apps)
    val splitted = data1.toList().groupBy {
        it.first.programName.split(":")[1] // Name of data structure
    }.mapValues { it.value.toMap() }

    for (kvp in splitted) {
        val data = kvp.value.mapKeys {
            it.key.toString().split(":")[0] // We only save before the separator
        }
        // val suffix = kvp.value.hashCode()
        val file = File("benchmarkoutput-${kvp.key}.json")
        file.writeText(gson.toJson(data))
    }
    */

    /* GENERATING A BENCHMARK
    val functions = listOf(
            Pair("1", { test1() }),
            Pair("2", { test2() }),
            Pair("3", { test3() }))

    val data = r.runWithoutInterleaving(1000, functions)

    val suffix = data.hashCode()
    val file = File("benchmarkoutput-warmup-$suffix.json")
    file.writeText(gson.toJson(data))

    val data1 = r.runWithInterleaving(1000, functions)
    val suffix1 = data1.hashCode()
    val file1 = File("benchmarkoutput-interleaved-warmup-$suffix1.json")
    file1.writeText(gson.toJson(data1))
    */
}
