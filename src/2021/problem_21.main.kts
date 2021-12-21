#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun Iterator<Int>.roll() = (0..2).sumOf { next() }

val positions = lines.map { it.split(": ").mapNotNull { it.toIntOrNull() }.single() - 1 }.toMutableList()
val scores = mutableListOf(0, 0)
val dice = generateSequence(1) { 1 + (it % 100) }.iterator()
var turnCount = 0

while (true) {
    turnCount++
    val roll0 = dice.roll()
    positions[0] = (positions[0] + roll0) % 10
    scores[0] += positions[0] + 1
    if (scores[0] >= 1000) break

    turnCount++
    val roll1 = dice.roll()
    positions[1] = (positions[1] + roll1) % 10
    scores[1] += positions[1] + 1
    if (scores[1] >= 1000) break
}

println(scores.minOf { it} * (turnCount * 3))
