#!/usr/bin/env kotlin

fun Long.strLen() = toString().length
fun Long.repeat(n: Int) = toString().repeat(n).toLong()

fun findInvalid(start: Long, end: Long, n: Int): Set<Long> = buildSet {
    if ((start.strLen()..end.strLen()).all { it % n != 0 }) return@buildSet
    var seed = start.toString().run { if (length <= n - 1) 1L else take(length / n).toLong() }
    while (seed.repeat(n) <= end) {
        if (seed.repeat(n) >= start) {
            add(seed.repeat(n))
        }
        seed++
    }
}

fun sumInvalid(start: Long, end: Long) =
    (2..end.strLen()).flatMap { findInvalid(start, end, it) }.toSet().sum()

fun solve(input: String) {
    val ranges = input.split(",").map { it.split("-").map { it.toLong() } }
    println(ranges.sumOf { findInvalid(it[0], it[1], 2).sum() })
    println(ranges.sumOf { sumInvalid(it[0], it[1]) })
}

solve(java.io.File(args[0]).readLines().single())
