#!/usr/bin/env kotlin

fun String.splitInTwo() = listOf(
    substring(0, length / 2).toLong(), 
    substring(length / 2, length).toLong()
)

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
