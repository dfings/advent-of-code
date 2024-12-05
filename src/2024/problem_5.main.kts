#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val rules = lines.filter { '|' in it }.map { it.split("|") }
val rulesForward = rules.groupBy({ it[0] }, { it[1] })
val rulesReversed = rules.groupBy({ it[1] }, { it[0] })
val updates = lines.filter { ',' in it }.map { it.split(",") }

fun findInvalidIndex(update: List<String>): Int {
    val invalid = mutableSetOf<String>()
    for ((i, page) in update.withIndex()) {
        if (page in invalid) {
            return i
        }
        invalid += rulesReversed[page] ?: emptyList()
    }
    return -1
}

fun sortUpdate(update: List<String>): List<String> {
    val newUpdate = update.toMutableList()
    var i = findInvalidIndex(newUpdate)
    while (i != -1) {
        val rule = rulesForward.getValue(newUpdate[i])
        val j = newUpdate.indexOfFirst { it in rule }
        java.util.Collections.swap(newUpdate, i, j)
        i = findInvalidIndex(newUpdate)
    }
    return newUpdate
}

fun isValid(update: List<String>) = findInvalidIndex(update) == -1
fun List<String>.midpoint() = this[lastIndex / 2].toInt()

val (validUpdates, invalidUpdates) = updates.partition { isValid(it) }
println(validUpdates.sumOf { it.midpoint() })
println(invalidUpdates.map { sortUpdate(it) }.sumOf { it.midpoint() })
