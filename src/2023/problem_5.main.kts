#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class AlmanacEntry(val destination: Long, val source: Long, val offset: Long) {
    operator fun contains(n: Long) = n >= source && n < source + offset
}

fun Iterable<AlmanacEntry>.getDestination(n: Long): Long =
    find { n in it }?.run { n + destination - source } ?: n

fun Iterable<AlmanacEntry>.getSplits(r: LongRange): Iterable<Long> =
    (
        listOf(r.start, r.endInclusive + 1) +
            filter { it.source in r }.map { it.source } +
            filter { it.source + it.offset in r }.map { it.source + it.offset }
    ).toSortedSet()

fun Iterable<AlmanacEntry>.getDestinations(r: LongRange): List<LongRange> =
    getSplits(r).windowed(2).map { getDestination(it[0])..getDestination(it[1] - 1) }


var i = 0
val pages = List(7) { mutableListOf<AlmanacEntry>() }
for (line in lines.drop(3)) {
    when {
        line.isBlank() -> i++
        line[0].isDigit() ->
            line.split(" ").map { it.toLong() }.let {
                pages[i].add(AlmanacEntry(it[0], it[1], it[2]))
            }
    }
}

val seeds = lines[0].substringAfter(": ").split(" ").map { it.toLong() }
val locations = seeds.map { seed -> pages.fold(seed) { acc, page -> page.getDestination(acc) } }
println(locations.min())

val seedRanges = seeds.chunked(2).map { it[0] until it[0] + it[1] }
val locationRanges = pages.fold(seedRanges) { acc, page -> acc.flatMap { page.getDestinations(it) } }
println(locationRanges.minOf { it.start })