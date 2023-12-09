#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun List<Long>.getSequences() =
    generateSequence(this) { it.windowed(2) { (a, b) -> b - a } }
        .takeWhile { it.any { it != 0L } }.toList()

fun List<List<Long>>.nextValue() = foldRight(0L) { it, acc -> it.last() + acc }
fun List<List<Long>>.previousValue() = foldRight(0L) { it, acc -> it.first() - acc }

val input = lines.map { it.split(" ").map { it.toLong() }.getSequences() }
println(input.map { it.nextValue() }.sum())
println(input.map { it.previousValue() }.sum())
