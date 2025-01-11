#!/usr/bin/env kotlin

class Jug(val liters: Int)

val lines = java.io.File(args[0]).readLines()
val jugs = lines.map { Jug(it.toInt()) }

fun fitCheck(used: List<Jug>, unused: List<Jug>, remaining: Int): List<List<Jug>> {
    if (remaining == 0) {
        return listOf(used)
    }
    val output = mutableListOf<List<Jug>>()
    for ((i, jug) in unused.withIndex()) {
        if (jug.liters <= remaining) {
            output.addAll(fitCheck(used + jug, unused.drop(i + 1), remaining - jug.liters))
        }
    }
    return output
}

val combos = fitCheck(emptyList(), jugs, 150)
println(combos.size)
val minJugs = combos.minOf { it.size }
println(combos.count { it.size == minJugs })
