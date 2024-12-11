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


val line = java.io.File(args[0]).readLines().single()
var stones = line.split(" ").map { it.toLong()}

println(stones)
repeat (25) {
    stones = stones.flatMap { blink(it) }
    println(stones)
}
println(stones.size)
