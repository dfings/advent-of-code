#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val pairs = lines.map { it.split("-").toSet() }.toSet()

val links = pairs.flatMap { listOf(it.toList(), it.toList().reversed()) }
    .groupBy({ it[0] }, { it[1 ]})
    .mapValues { it.value.toSet() }

fun Set<String>.intersectLinks() = map { links.getValue(it) }.reduce(Set<String>::intersect)
fun Set<Set<String>>.next() = flatMap { clique ->
    clique.intersectLinks().map { clique + it }
}.toSet()

var triples = pairs.next()
println(triples.count { clique -> clique.any { it.startsWith("t") } })

var maximal = triples
while (maximal.size > 1) {
    maximal = maximal.next()
}
println(maximal.single().sorted().joinToString(","))
