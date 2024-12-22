#!/usr/bin/env kotlin

import kotlin.time.measureTime

fun mixAndPrune(v: Long, s: Long) = (v xor s) % (1 shl 24)
fun next(s: Long): Long {
    val a = mixAndPrune(s shl 6, s)
    val b = mixAndPrune(a shr 5, a)
    return mixAndPrune(b shl 11, b)
}

fun secretNumbers(s: Long) = generateSequence(s, ::next)

fun getPriceSequenceMap(s: Long): Map<String, Int> {
    val prices = secretNumbers(s).map { (it % 10).toInt() }.take(2000)
    val changes = prices.zipWithNext().map { (a, b) -> b - a }.windowed(4)
    val output = mutableMapOf<String, Int>()
    changes.zip(prices.drop(4)).forEach { (k, v) -> output.putIfAbsent("$k", v) }
    return output
}

val initial = java.io.File(args[0]).readLines().map { it.toLong() }
println(initial.sumOf { secretNumbers(it).drop(2000).take(1).single() })

val histograms = initial.map { getPriceSequenceMap(it) }
val candidates = histograms.asSequence().flatMap { it.keys }.toSet()
println(candidates.maxOf { c -> histograms.sumOf { it[c] ?: 0 } })

val t = measureTime {
    candidates.maxOf { c -> histograms.sumOf { it[c] ?: 0 } }
}
println(t)
