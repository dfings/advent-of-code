#!/usr/bin/env kotlin

fun maxJoltage(bank: String): Int {
    val first = bank.dropLast(1).maxBy { "$it".toInt() }
    val firstIndex = bank.indexOf(first)
    val second = bank.drop(firstIndex + 1).maxBy { "$it".toInt() }
    return "$first$second".toInt()
}

fun solve(input: List<String>) {
    println(input.sumOf { maxJoltage(it) })
}

solve(java.io.File(args[0]).readLines())
