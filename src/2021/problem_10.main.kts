#!/usr/bin/env kotlin

val legalPairs = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
val corruptionScores = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
val completionScores = mapOf('(' to 1, '[' to 2, '{' to 3, '<' to 4)

sealed interface LineState
object Ok: LineState
class Corrupt(val score: Int): LineState
class Incomplete(val score: Long): LineState

fun String.detectCorruption(): LineState {
    val stack = ArrayDeque<Char>()
    toList().forEach {
        if (it in legalPairs.keys) {
            stack.addFirst(it)
        } else if (legalPairs[stack.removeFirst()] != it) {
            return Corrupt(corruptionScores.getValue(it))
        }
    }
    return if (stack.isEmpty()) {
        Ok
    } else {
        Incomplete(stack.fold(0L) { acc, it -> acc * 5 + completionScores.getValue(it) })
    }
}

val states = java.io.File(args[0]).readLines().map { it.detectCorruption() }
println(states.filterIsInstance<Corrupt>().sumOf { it.score })

val incompleteScores = states.filterIsInstance<Incomplete>().map { it.score }.sorted()
println(incompleteScores[incompleteScores.size / 2])
