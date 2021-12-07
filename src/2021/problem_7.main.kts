#!/usr/bin/env kotlin

import kotlin.math.abs

val crabs = java.io.File(args[0]).readLines().first().split(",").map { it.toInt() }
val range = (crabs.minOf { it }..crabs.maxOf { it })

fun linearTotalCost(x: Int) = crabs.sumOf { abs(it - x) }
println(range.minOf(::linearTotalCost))

fun increasingTotalCost(x: Int) = crabs.sumOf { (1..abs(it - x)).sum() }
println(range.minOf(::increasingTotalCost))
