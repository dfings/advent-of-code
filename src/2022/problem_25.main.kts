#!/usr/bin/env kotlin

val charToDigit = mapOf('2' to 2L, '1' to 1L, '0' to 0L, '-' to -1L, '=' to -2L)

fun String.parseSnafu() : Long {
    var place = 1L
    var result = 0L
    for (i in lastIndex downTo 0) {
        result += place * charToDigit.getValue(get(i))
        place *= 5L
    }
    return result
}

fun rangeOf(prefix: String, remainingDigits: Int) =
   (prefix + "=".repeat(remainingDigits)).parseSnafu()..
   (prefix + "2".repeat(remainingDigits)).parseSnafu()

fun Long.encodeSnafu() : String {
    var place = 1L
    var digits = 1
    while (this > "2".repeat(digits).parseSnafu()) {
        digits++
        place *= 5L
    }
    var prefix = ""
    while (digits > 0) {
        digits--
        prefix += charToDigit.keys.find { this in rangeOf(prefix + it, digits) }
    }
    return prefix
}

fun solve(lines: List<String>) {
    val total = lines.map { it.parseSnafu() }.sum()
    println(total.encodeSnafu())
}

solve(java.io.File(args[0]).readLines())
