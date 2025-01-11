#!/usr/bin/env kotlin

class Jug(val liters: Int)

val lines = java.io.File(args[0]).readLines()
val jugs = lines.map { Jug(it.toInt()) }

fun fitCheck(used: List<Jug>, unused: List<Jug>, remaining: Int): List<List<Jug>> {
    if (remaining == 0) {
        return listOf(used)
    }
    val canUse = unused.filter { it.liters <= remaining }
    return canUse.withIndex().flatMap { (i, jug) -> 
        fitCheck(used + jug, canUse.drop(i + 1), remaining - jug.liters) 
    }
}

val combos = fitCheck(emptyList(), jugs, 150)
println(combos.size)
val minJugs = combos.minOf { it.size }
println(combos.count { it.size == minJugs })
