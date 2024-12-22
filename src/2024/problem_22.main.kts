#!/usr/bin/env kotlin

import kotlin.time.measureTime

fun mixAndPrune(v: Long, s: Long) = (v xor s) % (1 shl 24)
fun next(s: Long): Long {
    val a = mixAndPrune(s shl 6, s)
    val b = mixAndPrune(a shr 5, a)
    return mixAndPrune(b shl 11, b)
}

fun secretNumbers(s: Long) = generateSequence(s, ::next)


fun IntArray.addPriceSequences(s: Long) {
    val prices = secretNumbers(s).map { (it % 10).toInt() }.take(2000)
    val changes = prices.zipWithNext()
        .map { (a, b) -> (b - a).mod(19) }
        .windowed(4)
        .map { (a, b, c, d) -> a * 6859 + b * 361 + c * 19 + d }
    for ((k, v) in changes.zip(prices.drop(4)).distinctBy { it.first }) {
        this[k] += v
    }
}

val initialValues = java.io.File(args[0]).readLines().map { it.toLong() }
println(initialValues.sumOf { secretNumbers(it).drop(2000).take(1).single() })

val t = measureTime {
val sequencePrices = IntArray(19 * 19 * 19 * 19) { 0 }
initialValues.map { sequencePrices.addPriceSequences(it) }
println(sequencePrices.max())
}
println(t)
