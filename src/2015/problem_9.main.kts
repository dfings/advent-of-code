#!/usr/bin/env kotlin

import kotlin.math.min
import kotlin.math.max

val pattern = Regex("""(\w+) to (\w+) = (\d+)""")

val lines = java.io.File(args[0]).readLines()
val costs = mutableMapOf<String, MutableMap<String, Int>>()
for (line in lines) {
    val (first, second, cost) = pattern.find(line)!!.destructured
    costs.getOrPut(first) { mutableMapOf<String, Int>() }.put(second, cost.toInt())
    costs.getOrPut(second) { mutableMapOf<String, Int>() }.put(first, cost.toInt())
}

fun findPath(path: List<String>, cost: Int, eval: (Int, Int) -> Int): Int {
    if (path.size == costs.size) return cost
    val next = costs.keys - path
    val nextCost = costs.getValue(path.last())
    return next.map { findPath(path + it, cost + nextCost.getValue(it), eval) }.reduce(eval)
}


println(costs.keys.minOf { findPath(listOf(it), 0, ::min) })
println(costs.keys.maxOf { findPath(listOf(it), 0, ::max) })
