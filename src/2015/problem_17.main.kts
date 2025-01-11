#!/usr/bin/env kotlin

class Jug(val liters: Int)

val lines = java.io.File(args[0]).readLines()
val jugs = lines.map { Jug(it.toInt()) }

val cache = mutableMapOf<Pair<List<Jug>, Int>, Int>()
fun fitCheck(
    used: List<Jug>, 
    unused: List<Jug>, 
    remaining: Int
): Int = cache.getOrPut(used to remaining) {
    if (remaining == 0) return@getOrPut 1 
    var total = 0
    for ((i, jug) in unused.withIndex()) {
        if (jug.liters <= remaining) {
            total += fitCheck(used + jug, unused.drop(i + 1), remaining - jug.liters)
        }
    }
    return@getOrPut total
}

println(fitCheck(emptyList(), jugs, 150))
val combos = cache.keys.filter { it.second == 0 }.map { it.first }
val minJugs = combos.minOf { it.size }
println(combos.count { it.size == minJugs })
