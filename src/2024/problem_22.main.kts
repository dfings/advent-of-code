#!/usr/bin/env kotlin

fun mixAndPrune(v: Long, s: Long) = (v xor s) % (1 shl 24)
fun next(s: Long): Long {
    val a = mixAndPrune(s shl 6, s)
    val b = mixAndPrune(a shr 5, a)
    return mixAndPrune(b shl 11, b)
}

fun secretNumbers(s: Long) = generateSequence(s, ::next)

fun IntArray.addPrices(s: Long) {
    val prices = secretNumbers(s).map { (it % 10).toInt() }.take(2000).toList()
    val seen = BooleanArray(130321) { false }
    for (i in 0..prices.lastIndex - 4) {
        val v = (prices[i] - prices[i + 1]).mod(19) * 6859 +
            (prices[i + 1] - prices[i + 2]).mod(19) * 361 +
            (prices[i + 2] - prices[i + 3]).mod(19) * 19
            (prices[i + 3] - prices[i + 4]).mod(19)
        if (!seen[v]) {
            seen[v] = true
            this[v] += prices[i + 4]
        }
    }
}

val initialValues = java.io.File(args[0]).readLines().map { it.toLong() }
println(initialValues.sumOf { secretNumbers(it).drop(2000).take(1).single() })

val priceChangesToPrice = IntArray(130321) { 0 }
for (initialValue in initialValues) {
    priceChangesToPrice.addPrices(initialValue)
}
println(priceChangesToPrice.max())
