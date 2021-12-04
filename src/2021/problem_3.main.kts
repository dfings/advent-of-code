#!/usr/bin/env kotlin

import java.io.File

val data = File(args[0]).readLines().map { it.toList().map(Character::getNumericValue) }

// Part 1
val bitCount = data.reduce { acc, it -> acc.zip(it, Int::plus) }
val cutoff = data.size / 2
val gamma = bitCount.map { if (it > cutoff) 1 else 0 }.joinToString("")
val epsilon = bitCount.map { if (it < cutoff) 1 else 0  }.joinToString("")
println(gamma.toInt(2) * epsilon.toInt(2))

// Part 2
fun getRating(subset: List<List<Int>>, mostCommon: Boolean, position: Int = 0): List<Int> {
    return if (subset.size == 1) {
        subset[0]
    } else {
        val setCount = subset.count { it[position] == 1 }
        val selected = when {
            setCount >= subset.size / 2 && mostCommon -> 1
            setCount < subset.size / 2 && !mostCommon -> 1
            else -> 0
        }
        getRating(subset.filter { it[position] == selected }, mostCommon, position + 1)
    }
}
val oxygen = getRating(data, mostCommon = true).joinToString("")
val co2 = getRating(data, mostCommon = false).joinToString("")
println(oxygen.toInt(2) * co2.toInt(2))
