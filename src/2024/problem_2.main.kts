#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val reports = lines.map { it.split(" ").map { it.toInt() } }

// Part 1
fun Pair<Int, Int>.isSafe() = second - first in 1..3
fun List<Int>.isSafe() = zipWithNext().all { it.isSafe() }
println(reports.count { it.isSafe() || it.reversed().isSafe() })

// Part 2
fun List<Int>.omit(index: Int) = filterIndexed { i, _ -> i != index }
fun List<Int>.isSafe2() : Boolean {
    val badIndex = zipWithNext().indexOfFirst { !it.isSafe() }
    return badIndex == -1 || omit(badIndex).isSafe() || omit(badIndex + 1).isSafe()
}
println(reports.count { it.isSafe2() || it.reversed().isSafe2() })
