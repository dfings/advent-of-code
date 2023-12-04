#!/usr/bin/env kotlin

import kotlin.math.min
import kotlin.math.pow

val lines = java.io.File(args[0]).readLines()

fun Int.pow(exp: Int) = toDouble().pow(exp.toDouble()).toInt()

fun String.numWinners(): Int {
    val (picks, winners) =
        split(':', '|')
            .drop(1)
            .map { it.trim().split(Regex("\\s+")).map { it.toInt() } }
    return (picks intersect winners).size
}

println(lines.map { 2.pow(it.numWinners() - 1) }.sum())

val numCards = (0..lines.lastIndex).map { 1 }.toMutableList()
lines.forEachIndexed { index, line ->
    val count = line.numWinners()
    for (i in index + 1..min(index + count, lines.lastIndex)) {
        numCards[i] += numCards[index]
    }
}
println(numCards.sum())
