#!/usr/bin/env kotlin

import kotlin.math.min

val lines = java.io.File(args[0]).readLines()

fun String.numWinners(): Int {
    val (winners, picks) =
        split(':', '|')
            .drop(1)
            .map { it.trim().split(Regex(" +")).map { it.toInt() } }
    return (picks intersect winners).size
}

println(lines.map { 1 shl (it.numWinners() - 1) }.sum())

val numCards = (0..lines.lastIndex).map { 1 }.toMutableList()
lines.forEachIndexed { index, line ->
    val count = line.numWinners()
    for (i in index + 1..min(index + count, lines.lastIndex)) {
        numCards[i] += numCards[index]
    }
}
println(numCards.sum())
