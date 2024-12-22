#!/usr/bin/env kotlin

fun mixAndPrune(v: Long, s: Long) = (v xor s) % (1 shl 24)
fun next(s: Long): Long {
    var v = mixAndPrune(s shl 6, s)
    v = mixAndPrune(v shr 5, v)
    v = mixAndPrune(v shl 11, v)
    return v
}

fun secretNumbers(s: Long) = generateSequence(s, ::next)

fun getPriceSequenceMap(s: Long): Map<List<Int>, Int> {
    val prices = secretNumbers(s).map { (it % 10).toInt() }.take(2000)
    val changes = prices.zipWithNext().map { (a, b) -> b - a }.windowed(4)
    val output = mutableMapOf<List<Int>, Int>()
    changes.zip(prices.drop(4)).forEach { (k, v) -> output.putIfAbsent(k, v) }
    return output
}

val initial = java.io.File(args[0]).readLines().map { it.toLong() }
println(initial.sumOf { secretNumbers(it).drop(2000).take(1).single() })

val histograms = initial.map { getPriceSequenceMap(it) }
val candidates = histograms.asSequence().flatMap { it.keys }.toSet()
val candidatePrices = candidates.map { c -> c to histograms.sumOf { it[c] ?: 0 }}
println(candidatePrices.maxOf { it.second })

