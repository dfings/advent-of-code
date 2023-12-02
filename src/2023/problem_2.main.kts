#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Reveal(val red: Int, val green: Int, val blue: Int)

fun String.toReveal(): Reveal {
    val map = trim().split(", ").map { it.split(' ') }.associate { it[1] to it[0].toInt() }
    return Reveal(map["red"] ?: 0, map["green"] ?: 0, map["blue"] ?: 0)
}

val games = lines.map { it.split(':', ';').drop(1).map { it.toReveal() } }

val maxRevealed =
    games.map { reveals ->
        Reveal(reveals.maxOf { it.red }, reveals.maxOf { it.green }, reveals.maxOf { it.blue })
    }

val matchingLines =
    maxRevealed.mapIndexed { index, it ->
        if (it.red > 12 || it.green > 13 || it.blue > 14) 0 else index + 1
    }

println(matchingLines.sum())

println(maxRevealed.map { it.red * it.green * it.blue }.sum())
