#!/usr/bin/env kotlin

import kotlin.math.abs

val crabs = java.io.File(args[0]).readLines().first().split(",").map { it.toInt() }
val range = (crabs.minOrNull()!!..crabs.maxOrNull()!!)

fun linearTotalCost(x: Int) = crabs.sumOf { abs(it - x) }
println(range.map(::linearTotalCost).minOrNull())

fun increasingTotalCost(x: Int) = crabs.sumOf { (1..abs(it - x)).sum() }
println(range.map(::increasingTotalCost).minOrNull())
