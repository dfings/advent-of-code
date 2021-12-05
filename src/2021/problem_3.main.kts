#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines().map { it.toList().map(Character::getNumericValue) }

fun List<Int>.toBinaryInt() = this.joinToString("").toInt(2)

// Part 1
// Count of 1 bits at each position (complement is unsetCount).
val setCount: List<Int> = lines.reduce { acc, it -> acc.zip(it, Int::plus) }
val cutoff = lines.size / 2
val gamma = setCount.map { if (it >= cutoff) 1 else 0 }.toBinaryInt()
val epsilon = setCount.map { if (it < cutoff) 1 else 0  }.toBinaryInt()
println(gamma * epsilon)

// Part 2
fun getRating(subset: List<List<Int>>, position: Int = 0, bitSelector: (Boolean) -> Int): Int {
    return if (subset.size == 1) {
        subset[0].toBinaryInt()
    } else {
        val positionSetCount = subset.count { it[position] == 1 }
        val positionFilter = bitSelector(positionSetCount >= subset.size / 2)
        getRating(subset.filter { it[position] == positionFilter }, position + 1, bitSelector)
    }
}

val oxygen = getRating(lines) { if (it) 1 else 0 }
val co2 = getRating(lines) { if (it) 0 else 1 }
println(oxygen * co2)
