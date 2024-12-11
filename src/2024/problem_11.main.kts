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

data class CacheKey(val stone: Long, val i: Int, val limit: Int)
val cache = mutableMapOf<CacheKey, Long>()
fun countStones(stone: Long, i: Int, limit: Int): Long = cache.getOrPut(CacheKey(stone, i, limit)) {
    if (i == limit) 1 else blink(stone).sumOf { countStones(it, i + 1, limit)}
}

val line = java.io.File(args[0]).readLines().single()
val stones = line.split(" ").map { it.toLong()}
println(stones.sumOf { countStones(it, 0, 25) })
println(stones.sumOf { countStones(it, 0, 75) })
