#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

val winnerCounts =
    lines.map {
        val (winners, picks) =
            it.split(':', '|')
                .drop(1)
                .map { it.trim().split(Regex(" +")).map { it.toInt() } }
        (picks intersect winners).size
    }

println(winnerCounts.map { 1 shl (it - 1) }.sum())

val numCards = MutableList(lines.size) { 1 }
winnerCounts.forEachIndexed { index, count ->
    for (i in index + 1..index + count) {
        numCards[i] += numCards[index]
    }
}
println(numCards.sum())
