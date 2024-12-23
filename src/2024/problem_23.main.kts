#!/usr/bin/env kotlin

import java.util.BitSet
import kotlin.streams.asSequence

fun String.encode() = (this[0] - 'a') * 26 + (this[1] - 'a')
fun Int.decode() = "${'a' + (this / 26)}${'a' + (this % 26)}"

fun makeBitSet(bitIndexes: Iterable<Int> = emptyList()) = 
    BitSet(26 * 26).apply { bitIndexes.forEach { set(it) } }

fun BitSet.bitIndexes() = stream().asSequence()
fun BitSet.intersect(other: BitSet) = (clone() as BitSet).also { it.and(other) } 
operator fun BitSet.plus(index: Int) = (clone() as BitSet).also { it.set(index ) }

val lines = java.io.File(args[0]).readLines()
val pairs = lines.map { makeBitSet(it.split("-").map { it.encode() }) }.toSet()

val links = pairs.flatMap { listOf(it.bitIndexes().toList(), it.bitIndexes().toList().reversed()) }
    .groupBy({ it[0] }, { it[1 ]})
    .mapValues { makeBitSet(it.value) }

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
