#!/usr/bin/env kotlin

import kotlin.math.max

fun mixAndPrune(v: Long, s: Long) = (v xor s) % (1 shl 24)

fun nextSecretNumber(s: Long): Long {
    var v = mixAndPrune(s shl 6, s)
    v = mixAndPrune(v shr 5, v)
    v = mixAndPrune(v shl 11, v)
    return v
}

fun secretNumbers(s: Long) = generateSequence(s) { nextSecretNumber(it) }
fun prices(s: Long) = secretNumbers(s).map { (it % 10).toInt() }

fun getPriceSequenceMap(s: Long): Map<List<Int>, Int> {
    val p = prices(s)
    val changes = p.zipWithNext().map { (a, b) -> b - a }
    val changeWindows = changes.take(2000).windowed(4)
    val output = mutableMapOf<List<Int>, Int>()
    changeWindows.zip(p.drop(4)).forEach { (k, v) -> output.putIfAbsent(k, v) }
    return output
}

val lines = java.io.File(args[0]).readLines()
val initialSecretNumbers = lines.map { it.toLong() }
println(initialSecretNumbers.sumOf { secretNumbers(it).drop(2000).take(1).single() })

val histograms = initialSecretNumbers.map { getPriceSequenceMap(it) }
val candidates = histograms.asSequence().flatMap { it.keys }.toSet()
val candidatePrices = candidates.map { c -> c to histograms.sumOf { it[c] ?: 0 }}
println(candidatePrices.maxOf { it.second })

