#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val initial = lines.first()
val rules = lines.drop(2).map { it.split(" -> ").let { it[0] to it[1] } }.toMap()

fun String.insertPairs() = windowed(2).map { pair -> 
    rules[pair]?.let { pair[0] + it + pair[1] } ?: pair
}.reduce { acc, it -> acc + it.drop(1) }

val round10 = (1..10).fold(initial) { acc, it -> println(it); acc.insertPairs() }
val histogram = round10.groupingBy { it }.eachCount()
println(histogram.maxOf { it.value } - histogram.minOf { it.value })
