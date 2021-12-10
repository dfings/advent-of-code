#!/usr/bin/env kotlin

val legalPairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
val scores = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)

val lines = java.io.File(args[0]).readLines()

fun String.detectCorruption(): Char? {
    val stack = ArrayDeque<Char>()
    toList().forEach {
        if (it in legalPairs.keys) {
            stack.addFirst(it)
        } else if (legalPairs[stack.removeFirst()] != it) {
            return it
        }
    }
    return null
}


println(lines.mapNotNull { it.detectCorruption() }.sumOf { scores.getValue(it) })
