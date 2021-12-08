#!/usr/bin/env kotlin

val lengths = setOf(2, 3, 4, 7)
val outputValues = java.io.File(args[0]).readLines().map { it.split("|").last().split(" ") }
println(outputValues.sumOf { it.count {  it.length in lengths } })
