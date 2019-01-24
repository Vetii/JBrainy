package se.lth.cs

import java.util.*

// Some benchmarks
fun test1(): IntArray {
    val a = (0..1000).toList().toTypedArray()
    val b = IntArray(1000)

    // Synthetic piece of code to see if counters run as expected
    var acc = 0
    for (i in 0 until 1000) {
        acc += a[i]
        if(acc % 2 == 1) {
            b[i] = acc
        }
    }
    return b
}

fun test2(): LinkedList<Int> {
    var a = LinkedList<Int>()
    a.add(0)
    a.add(1)
    for (i in 0 .. 1000) {
        a.add(
                a.last + a.get(a.size - 2)
        )
    }
    return a
}

fun test3(): HashMap<Int, MutableList<Int>> {
    val a = HashMap<Int, MutableList<Int>>()
    for (i in 0 .. 1000) {
        for (j in 2 .. 9) {
            if (i % j == 0) {
                if (a.containsKey(j)) {
                    a[j]?.add(i)
                } else {
                    a[j] = mutableListOf()
                }
            }
        }
    }
    return a
}

// Some utility functions for stats
fun median(l : List<Float>) : Float {
    return l.sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }
}

fun medianLong(l : List<Long>) : Float {
    return median(l.map{ it.toFloat() })
}

