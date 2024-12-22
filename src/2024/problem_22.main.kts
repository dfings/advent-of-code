#!/usr/bin/env kotlin

fun mixAndPrune(x: Long, y: Long) = (x xor y) and 0xFFFFFF
fun next(current: Long): Long {
    val a = mixAndPrune(current shl 6, current)
    val b = mixAndPrune(a shr 5, a)
    return mixAndPrune(b shl 11, b)
}

fun getIndex(initialValue: Long, index: Int): Long {
    var current = initialValue
    repeat (index) {
        current = next(current)
    }
    return current
}

fun IntArray.addPrices(initialValue: Long) {
    val prices = IntArray(2001)
    var current = initialValue
    for (i in 0..2000) {
        prices[i] = (current % 10).toInt()
        current = next(current)
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
println(initialValues.sumOf { getIndex(it, 2000) })

val priceChangesToPrice = IntArray(130321)
for (initialValue in initialValues) {
    priceChangesToPrice.addPrices(initialValue)
}
println(priceChangesToPrice.max())
