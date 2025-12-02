#!/usr/bin/env kotlin

fun Long.repeat(n: Int) = toString().repeat(n).toLong()

fun findInvalid(start: Long, end: Long, n: Int): Set<Long> = buildSet {
    val startStr = start.toString()
    var seed = if (startStr.length <= n - 1) 1 else startStr.take(startStr.length / n).toLong()
    while (seed.repeat(n) <= end) {
        if (seed.repeat(n) >= start) {
            add(seed.repeat(n))
        }
        seed++
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
