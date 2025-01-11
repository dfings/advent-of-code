#!/usr/bin/env kotlin

class Jug(val liters: Int)

val lines = java.io.File(args[0]).readLines()
val jugs = lines.map { Jug(it.toInt()) }

val combos = mutableListOf<List<Jug>>()
fun fitCheck(used: List<Jug>, unused: List<Jug>, remaining: Int): Int {
    if (remaining == 0) {
        combos.add(used)
        return 1 
    }
    var total = 0
    for ((i, jug) in unused.withIndex()) {
        if (jug.liters <= remaining) {
            total += fitCheck(used + jug, unused.drop(i + 1), remaining - jug.liters)
        }
    }
    return total
}

println(fitCheck(emptyList(), jugs, 150))
val minJugs = combos.minOf { it.size }
println(combos.count { it.size == minJugs })
