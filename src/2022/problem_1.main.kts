#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

val calorieCount = mutableListOf(0)
lines.forEach { line ->
    if (line.isBlank()) {
        calorieCount.add(0)
    } else {
        calorieCount[calorieCount.lastIndex] += line.toInt()
    }
}
println(calorieCount.max())
println(calorieCount.sortedDescending().take(3).sum())
