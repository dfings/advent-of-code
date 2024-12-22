#!/usr/bin/env kotlin

fun mixAndPrune(v: Long, s: Long) = (v xor s) % (1 shl 24)
fun next(s: Long): Long {
    val a = mixAndPrune(s shl 6, s)
    val b = mixAndPrune(a shr 5, a)
    return mixAndPrune(b shl 11, b)
}

fun IntArray.addPrices(s: Long) {
    val prices = IntArray(2001)
    var v = s
    for (i in 0..2000) {
        prices[i] = (v % 10).toInt()
        v = next(v)
    }
    val seen = BooleanArray(130321)
    for (i in 0..prices.lastIndex - 4) {
        val v = (prices[i + 1] - prices[i] + 9) * 6859 +
            (prices[i + 2] - prices[i + 1] + 9) * 361 +
            (prices[i + 3] - prices[i + 2] + 9) * 19 +
            (prices[i + 4] - prices[i + 3] + 9)
        if (!seen[v]) {
            seen[v] = true
            this[v] += prices[i + 4]
        }
    }
}

val initialValues = java.io.File(args[0]).readLines().map { it.toLong() }
println(initialValues.sumOf { generateSequence(it, ::next).drop(2000).take(1).single() })

val priceChangesToPrice = IntArray(130321)
for (initialValue in initialValues) {
    priceChangesToPrice.addPrices(initialValue)
}
println(priceChangesToPrice.max())
