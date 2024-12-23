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
val pairs = lines.map { makeBitSet(it.split("-").map { it.encode() }) }.toSet()

val links = Array(26 * 26) { makeBitSet() }
for (pair in pairs) {
    for (bitIndex in pair.bitIndexes()) {
        links[bitIndex].or(pair)
        links[bitIndex].clear(bitIndex)
    }
}

fun BitSet.intersectLinks() = bitIndexes().map { links[it] }.reduce(BitSet::intersect)
fun Set<BitSet>.next() = flatMap { clique ->
    clique.intersectLinks().bitIndexes().map { clique + it }
}.toSet()

val triples = pairs.next()
val ts = makeBitSet("ta".encode().."tz".encode())
println(triples.count { it.intersects(ts) })

var maximalCliques = triples
while (maximalCliques.size > 1) {
    maximalCliques = maximalCliques.next()
}
println(maximalCliques.single().bitIndexes().sorted().map { it.decode() }.joinToString(","))
