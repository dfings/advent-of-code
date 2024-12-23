#!/usr/bin/env kotlin

import java.util.BitSet
fun String.encode() = (this[0] - 'a') * 26 + (this[1] - 'a')
fun Int.decode() = "${'a' + (this / 26)}${'a' + (this % 26)}"

fun makeBitSet(bitIndexes: Iterable<Int> = emptyList()) = 
    BitSet(26 * 26).apply { bitIndexes.forEach { set(it) } }

fun BitSet.bitIndexes() = stream().toArray()
fun BitSet.copy() = clone() as BitSet
fun BitSet.intersect(other: BitSet) = copy().apply { and(other) } 
fun BitSet.union(other: BitSet) = copy().apply { or(other) } 
operator fun BitSet.plus(index: Int) = copy().apply { set(index) }
operator fun BitSet.minus(index: Int) = copy().apply { set(index, false) }

val lines = java.io.File(args[0]).readLines()
val pairs = lines.map { makeBitSet(it.split("-").map { it.encode() }) }.toSet()

val links = pairs.flatMap { clique -> clique.bitIndexes().map { it to (clique - it) } }
    .groupingBy { it.first }
    .fold(makeBitSet()) { acc, it -> acc.union(it.second) }

fun BitSet.intersectLinks() = bitIndexes().map { links.getValue(it) }.reduce(BitSet::intersect)
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
