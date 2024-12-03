#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun MatchResult.mul() = groupValues[1].toInt() * groupValues[2].toInt()

// Part 1
val pattern = Regex("""mul\((\d+),(\d+)\)""")
println(lines.flatMap { pattern.findAll(it) }.map { it.mul() }.sum())

// Part 2
val pattern2 = Regex("""mul\((\d+),(\d+)\)|do\(\)|don't\(\)""")
var enabled = true
var total = 0
for (match in lines.flatMap { pattern2.findAll(it) }) {
    when (match.value) {
        "do()" -> enabled = true
        "don't()" -> enabled = false
        else -> if (enabled) total += match.mul()
    }
}
println(total)
