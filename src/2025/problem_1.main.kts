#!/usr/bin/env kotlin

import kotlin.math.abs

fun solve(lines: List<String>) {
    val turns = lines.map { (if (it[0] == 'L') -1 else 1) * it.drop(1).toInt() }
    val positions = turns.runningFold(50) { acc, it -> (acc + it).mod(100) }
    println(positions.count { it == 0 })

    val fullTurns = turns.sumOf { abs(it) / 100 }
    val partialTurns = positions.zipWithNext().zip(turns).count { (pair, turn) ->
        val (a, b) = pair
        a != 0 && (b == 0 || ((turn < 0 && b > a) || (turn > 0 && b < a)))
    }
    println(fullTurns + partialTurns)
}

solve(java.io.File(args[0]).readLines())
