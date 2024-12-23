#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val links = lines.map { it.split("-") }

val connections = (links + links.map { it.reversed() }).groupBy({ it[0] }, { it[1] }).mapValues { it.value.toSet() }
var triples = mutableSetOf<Set<String>>()
for (first in connections.keys) {
    for (second in connections.getValue(first)) {
        for (third in connections.getValue(second)) {
            if (first in connections.getValue(third)) {
                triples.add(setOf(first, second, third))
            }
        }
    }
}
println(triples.count { it.any { it.startsWith("t") } })

fun Set<String>.fullyConnected() = map { connections.getValue(it) }.reduce(Set<String>::intersect)

fun Set<Set<String>>.next(): Set<Set<String>> {
    val n = mutableSetOf<Set<String>>()
    for (s in this) {
        for (candidate in s.fullyConnected()) {
            n.add(s + candidate)
        }
    }
    return n
}

val quads = triples.next()
val fives = quads.next()
val sixes = fives.next()
val sevens = sixes.next()
val eights = sevens.next()
val nines = eights.next()
val tens = nines.next()
val elevens = tens.next()
val twelves = elevens.next()
val thirteens = twelves.next()
println(thirteens.single().sorted().joinToString(","))
