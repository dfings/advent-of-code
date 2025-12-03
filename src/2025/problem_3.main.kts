#!/usr/bin/env kotlin

fun maxJoltage(bank: String, n: Int): String {
    if (n == 0) return ""
    val best = bank.dropLast(n - 1).max()
    val rest = maxJoltage(bank.drop(bank.indexOf(best) + 1), n - 1)
    return best + rest
}

fun solve(input: List<String>) {
    println(input.sumOf { maxJoltage(it, 2).toLong() })
    println(input.sumOf { maxJoltage(it, 12).toLong() })
}

solve(java.io.File(args[0]).readLines())
