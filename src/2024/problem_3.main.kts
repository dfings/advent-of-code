#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun MatchResult.mul() = groupValues[1].toInt() * groupValues[2].toInt()

// Part 1
val pattern = Regex("""mul\((\d+),(\d+)\)""")
println(lines.flatMap { pattern.findAll(it) }.sumOf { it.mul() })

// Part 2
val pattern2 = Regex("""mul\((\d+),(\d+)\)|do\(\)|don't\(\)""")
var enabled = true
val total = lines.flatMap { pattern2.findAll(it) }.sumOf {
    when (it.value) {
        "do()" -> enabled = true
        "don't()" -> enabled = false
    }
    if (enabled && it.value[0] == 'm') it.mul() else 0
}
println(total)
