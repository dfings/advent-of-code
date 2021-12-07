#!/usr/bin/env kotlin

import kotlin.math.abs

val crabs = java.io.File(args[0]).readLines().first().split(",").map { it.toInt() }
val low = crabs.minOrNull()!!
val high = crabs.maxOrNull()!!
println((low..high).map { x -> crabs.sumOf { abs(it - x) } }.minOrNull())

fun cost(x: Int) = (1..x).sum()
println((low..high).map { x -> crabs.sumOf { cost(abs(it - x)) } }.minOrNull())
