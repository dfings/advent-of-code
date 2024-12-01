#!/usr/bin/env kotlin

import kotlin.math.abs

val lines = java.io.File(args[0]).readLines()
val parsed = lines.map { it.split("   ").map { it.toInt() }}
val firstList = parsed.map { it[0] }.sorted()
val secondList = parsed.map { it[1] }.sorted()

// Part 1
val distances = firstList.zip(secondList).map { (x, y) -> abs(x - y) }
println(distances.sum())

// Part 2
val frequencies = secondList.groupBy { it }.mapValues { it.value.size }
val similarities = firstList.map { it * (frequencies[it] ?: 0) }
println(similarities.sum())
