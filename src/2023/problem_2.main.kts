#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Reveal(val red: Int, val green: Int, val blue: Int)

fun String.toReveal(): Reveal {
    val pulls = trim().split(", ").map { it.split(' ') }
    return Reveal(
        pulls.find { it[1] == "red" }?.get(0)?.toInt() ?: 0,
        pulls.find { it[1] == "green" }?.get(0)?.toInt() ?: 0,
        pulls.find { it[1] == "blue" }?.get(0)?.toInt() ?: 0,
    )
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
