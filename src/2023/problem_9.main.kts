#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun nextValue(start: List<Long>): Long {
    val seq = generateSequence(start) { it.windowed(2) { (a, b) -> b - a }}
    val lists = seq.takeWhile { it.any { it != 0L } }.toList()
    return lists.foldRight(0L) { it: List<Long>, acc: Long -> acc + it.last() }
}

val input = lines.map { it.split(" ").map { it.toLong() } }
println(input.map { nextValue(it) }.sum())
