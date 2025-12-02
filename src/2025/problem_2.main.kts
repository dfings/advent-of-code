#!/usr/bin/env kotlin

fun Long.repeat(n: Int) = List(n) { toString() }.joinToString("").toLong()

fun findInvalid(start: Long, end: Long, n: Int): Set<Long> = buildSet {
    val startString = start.toString()
    var toTest = if (startString.length <= n - 1) 1 else startString.take(startString.length / n).toLong()
    while (toTest.repeat(n) <= end) {
        if (toTest.repeat(n) >= start) {
            add(toTest.repeat(n))
        }
        toTest++
    }
}

fun sumInvalid(start: Long, end: Long) =
    (2..end.toString().length).flatMap { findInvalid(start, end, it) }.toSet().sum()

fun solve(input: String) {
    val ranges = input.split(",").map { it.split("-").map { it.toLong() } }
    println(ranges.sumOf { findInvalid(it[0], it[1], 2).sum() })
    println(ranges.sumOf { sumInvalid(it[0], it[1]) })
}

solve(java.io.File(args[0]).readLines().single())
