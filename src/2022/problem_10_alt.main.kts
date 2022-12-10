#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

// Value at the beginning of a given cycle.
val values = listOf(1) + lines.flatMap {
    if (it == "noop") listOf(0) else listOf(0, it.drop(5).toInt())
}.runningFold(1, Int::plus).dropLast(1)

println((20..240 step 40).sumOf { values[it] * it })

// No update on cycle 0.
values.drop(1).chunked(40).map {
    it.mapIndexed { i, x -> if (i - x in -1..1) '#' else '.' }
}.forEach { println(it.joinToString("")) }
