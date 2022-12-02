#!/usr/bin/env kotlin

val pairs = java.io.File(args[0]).readLines().map { it.split(" ").map { it[0] } }

// Rock = 0, Paper = 1, Scissors = 2
fun score1(opponentPlay: Int, selfPlay: Int): Int = 1 + selfPlay + when (selfPlay) {
    opponentPlay -> 3 // Draw
    (opponentPlay + 1) % 3 -> 6 // Win
    else -> 0 // Lose
}

fun score2(opponentPlay: Int, outcome: Char): Int = 1 + when (outcome) {
    'Z' -> (opponentPlay + 1) % 3 + 6 // Win
    'X' -> (opponentPlay + 2) % 3 // Lose
    else -> opponentPlay + 3 // Draw
}

println(pairs.sumBy { score1(it[0] - 'A', it[1] - 'X') })
println(pairs.sumBy { score2(it[0] - 'A', it[1]) })
