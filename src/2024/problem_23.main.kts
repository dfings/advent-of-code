#!/usr/bin/env kotlin

import java.util.BitSet

fun String.encode() = (this[0] - 'a') * 26 + (this[1] - 'a')
fun Int.decode() = "${'a' + (this / 26)}${'a' + (this % 26)}"

fun makeBitSet(bitIndexes: Iterable<Int> = emptyList()) = 
    BitSet(26 * 26).apply { bitIndexes.forEach { set(it) } }

fun BitSet.bitIndexes(): IntArray = stream().toArray()
fun BitSet.intersect(other: BitSet) = (clone() as BitSet).apply { and(other) } 
operator fun BitSet.plus(index: Int) = (clone() as BitSet).apply { set(index) }

val lines = java.io.File(args[0]).readLines()
val input = lines.map { it.split("-").map { it.encode() } }

val links = Array(26 * 26) { makeBitSet() }
for (link in input) {
    links[link[0]].set(link[1])
    links[link[1]].set(link[0])
}

fun BitSet.intersectLinks() = bitIndexes().map { links[it] }.reduce(BitSet::intersect)
fun Set<BitSet>.next() = flatMap { clique ->
    clique.intersectLinks().bitIndexes().map { clique + it }
}.toSet()

val triples = input.map { makeBitSet(it) }.toSet().next()
val ts = makeBitSet("ta".encode().."tz".encode())
println(triples.count { it.intersects(ts) })

var maximalCliques = triples
while (maximalCliques.size > 1) {
    maximalCliques = maximalCliques.next()
}
println(maximalCliques.single().bitIndexes().sorted().map { it.decode() }.joinToString(","))
