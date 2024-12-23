#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val links = lines.map { it.split("-") }

val connections = (links + links.map { it.reversed() }).groupBy({ it[0] }, { it[1] })
var triples = mutableSetOf<Set<String>>()
for (first in connections.keys.filter { it.startsWith("t") }) {
    for (second in connections.getValue(first)) {
        for (third in connections.getValue(second)) {
            if (first in connections.getValue(third)) {
                triples.add(setOf(first, second, third))
            }
        }
    }
}
println(triples.size)

