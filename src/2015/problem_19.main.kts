#!/usr/bin/env kotlin

data class Rule(val from: String, val to: String) {
    val fromRegex = from.toRegex()
}

fun successors(molecule: String, rules: List<Rule>): Set<String> {
    val output = mutableSetOf<String>()
    for (rule in rules) {
        for (range in rule.fromRegex.findAll(molecule).map { it.range }) {
            output += molecule.replaceRange(range.start, range.endInclusive + 1, rule.to)
        }
    }
    return output
}

fun reduce(start: String, end: String, rules: List<Rule>): Int {
    var current = start
    for (i in 0..Int.MAX_VALUE) {
        if (current == end) return i
        current = successors(current, rules).minBy { it.length }
    }
    return -1
}

val lines = java.io.File(args[0]).readLines()
val rules = lines.dropLast(2).map { it.split(" => ") }.map { Rule(it[0], it[1]) }
val molecule = lines.last()

println(successors(molecule, rules).size)
println(reduce(molecule, "e", rules.map { (from, to) -> Rule(to, from) }))
