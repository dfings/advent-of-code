#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun String.digitValue() = first { it.isDigit() }.digitToInt() * 10 + last { it.isDigit() }.digitToInt()

println(lines.sumBy { it.digitValue() })

val lookupTable =
    mapOf(
        "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
        "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
        "0" to 0, "1" to 1, "2" to 2, "3" to 3, "4" to 4,
        "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9,
    )

fun Pair<Int, String>?.toInt() = lookupTable.getValue(checkNotNull(this).second)

fun String.digitOrTextValue() = findAnyOf(lookupTable.keys).toInt() * 10 + findLastAnyOf(lookupTable.keys).toInt()

println(lines.sumBy { it.digitOrTextValue() })
