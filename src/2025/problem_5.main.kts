#!/usr/bin/env kotlin

fun String.toRange(): LongRange {
    val (a, b) = split("-").map { it.toLong() }
    return a..b
}

fun solve(input: List<String>) {
    val ranges = input.takeWhile { it.isNotEmpty() }.map { it.toRange() }
    val ingredients = input.dropWhile { it.isNotEmpty() }.drop(1).map { it.toLong() }
    println(ingredients.count { i -> ranges.any { r -> i in r } })
}

solve(java.io.File(args[0]).readLines())
