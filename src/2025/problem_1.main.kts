#!/usr/bin/env kotlin

fun solve(lines: List<String>) {
    val turns = lines.map { (if (it[0] == 'L') -1  else 1) * it.drop(1).toInt() }
    println(turns.runningFold(50) { acc, it -> (acc + it).mod(100) }.count { it == 0 })
}

solve(java.io.File(args[0]).readLines())
