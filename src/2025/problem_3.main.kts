#!/usr/bin/env kotlin

fun maxJoltage(bank: String, n: Int): Long {
    val best = bank.dropLast(n - 1).max()
    return if (n == 1) {
        "$best".toLong()
    } else {
        val rest = maxJoltage(bank.drop(bank.indexOf(best) + 1), n - 1)
        "$best$rest".toLong()
    }
}

fun solve(input: List<String>) {
    println(input.sumOf { maxJoltage(it, 2) })
    println(input.sumOf { maxJoltage(it, 12) })
}

solve(java.io.File(args[0]).readLines())
