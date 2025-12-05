#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

fun String.toRange(): LongRange {
    val (a, b) = split("-").map { it.toLong() }
    return a..b
}

fun overlap(a: LongRange, b: LongRange) = !(a.endInclusive < b.start || b.endInclusive < a.start)
fun merge(a: LongRange, b: LongRange) = min(a.start, b.start)..max(a.endInclusive, b.endInclusive)

fun solve(input: List<String>) {
    val ranges = input.takeWhile { it.isNotEmpty() }.map { it.toRange() }.toMutableSet()
    val ingredients = input.dropWhile { it.isNotEmpty() }.drop(1).map { it.toLong() }
    println(ingredients.count { i -> ranges.any { r -> i in r } })

    while (true) {
        val count = ranges.size
        for (a in ranges.toList()) {
            if (a !in ranges) continue
            val b = ranges.find { it != a && overlap(a, it) }
            if (b != null) {
                ranges.remove(a)
                ranges.remove(b)
                ranges.add(merge(a, b))
            }
        }
        if (count == ranges.size) break
    }
    println(ranges.sumOf { it.endInclusive - it.start + 1 })

}

solve(java.io.File(args[0]).readLines())
