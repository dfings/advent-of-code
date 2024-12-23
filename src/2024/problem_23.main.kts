#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val pairs = lines.map { it.split("-").toSet() }.toSet()

val links = mutableMapOf<String, MutableSet<String>>()
for (pair in pairs) {
    links.getOrPut(pair.first()) { HashSet<String>() }.add(pair.last())
    links.getOrPut(pair.last()) { HashSet<String>() }.add(pair.first())
}

fun Set<String>.fullyConnected() = map { links.getValue(it) }.reduce(Set<String>::intersect)
fun Set<Set<String>>.next(): Set<Set<String>> {
    val n = mutableSetOf<Set<String>>()
    for (s in this) {
        for (candidate in s.fullyConnected()) {
            n.add(s + candidate)
        }
    }
    return n
}

var triples = pairs.next()
println(triples.count { it.any { it.startsWith("t") } })

var maximal = triples
while (maximal.size > 1) {
    maximal = maximal.next()
}
println(maximal.single().sorted().joinToString(","))
