package se.lth.cs

import papi.EventSet

/**
 * Runs functions and gathers the performance counters, in Interleaved mode
 * (For each counter, run the full benchmark)
 */

class PapiInterleavedRunner(counterSpecification: CounterSpecification) : PapiRunner(counterSpecification) {

    /**
     * Runs a list of applications in "interleaved mode"
     * (that is, choose the counter first and run all application with this counter,
     * then pick another counter)
     */
    override fun runApplications(numRuns : Int, functions : List<Pair<String, () -> Any>>)
            : Map<String, MutableMap<String, List<Long>>> {
        var data : MutableMap<String, MutableMap<String, List<Long>>> = mutableMapOf()

        for (kvp in counterSpec.currentSpec) {
            val evset = EventSet.create(kvp.value)

            println("Interleaved mode: " + "'" + kvp.key + "'")
            // For each run-number
            for (run in 0..numRuns) {
                // We run each program
                for (function in functions) {
                    val appName = function.first
                    if (!data.containsKey(appName)) {
                        data[appName] = mutableMapOf()
                    }
                    // We do the measurements
                    evset.start()
                    val result = function.second()
                    evset.stop()

                    //println(result)
                    // We record the data
                    val counterdata = evset.counters
                    data[appName]?.put(kvp.key, counterdata.toList())
                }
            }
        }

        return data
    }
}