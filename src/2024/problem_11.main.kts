#!/usr/bin/env kotlin

fun String.splitInTwo() = listOf(take(length / 2).toLong(),  drop(length / 2).toLong())
fun blink(stone: Long) = when {
    stone == 0L -> listOf(1L)
    "$stone".length % 2 == 0 -> "$stone".splitInTwo()
    else -> listOf(stone * 2024)
}

val cache = mutableMapOf<Pair<Long, Int>, Long>()
fun countStones(stone: Long, blinksLeft: Int): Long = cache.getOrPut(stone to blinksLeft) {
    if (blinksLeft == 0) 1 else blink(stone).sumOf { countStones(it, blinksLeft - 1) }
}

val line = java.io.File(args[0]).readLines().single()
val stones = line.split(" ").map { it.toLong() }
println(stones.sumOf { countStones(it, 25) })
println(stones.sumOf { countStones(it, 75) })

// Alternate solution
fun Map<Long, Long>.blink(): Map<Long, Long> =
    entries.flatMap { (stone, count) -> blink(stone).map { it to count } }
        .groupBy({ it.first }, { it.second })
        .mapValues { (k, v) -> v.sum() }

var stoneCounts = stones.groupingBy { it }.eachCount().mapValues { it.value.toLong() }
repeat (25) { stoneCounts = stoneCounts.blink() }
println(stoneCounts.values.sum())
repeat (50) { stoneCounts = stoneCounts.blink() }
println(stoneCounts.values.sum())
