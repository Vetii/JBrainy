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
    override fun runApplications(numRuns: Int, applications: List<Application<*>>)
            : Map<Application<*>, MutableMap<String, List<Long>>> {
        var data : MutableMap<Application<*>, MutableMap<String, List<Long>>> = mutableMapOf()

        for (kvp in counterSpec.currentSpec) {
            val evset = EventSet.create(kvp.value)

            println("Interleaved mode: " + "'" + kvp.key + "'")
            // For each run-number
            for (run in 0..numRuns) {
                // We run each program
                for (app in applications) {
                    if (!data.containsKey(app)) {
                        data[app] = mutableMapOf()
                    }
                    // We do the measurements
                    evset.start()
                    val result = app.benchmark()
                    evset.stop()

                    //println(result)
                    // We record the data
                    val counterdata = evset.counters
                    data[app]?.put(kvp.key, counterdata.toList())
                }
            }
        }

        return data
    }
}