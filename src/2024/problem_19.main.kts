#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val towels = lines[0].split(", ")
val patterns = lines.drop(2)

val cache = mutableMapOf<String, Long>()
fun countTowelSequences(pattern: String, partial: String): Long = when {
    partial == pattern -> 1L
    !pattern.startsWith(partial) -> 0L
    else -> cache.getOrPut(pattern.drop(partial.length)) {
        towels.sumOf { countTowelSequences(pattern, partial + it) }
    }
}

val sequenceCounts = patterns.map { countTowelSequences(it, "") }
println(sequenceCounts.count {  it > 0 })
println(sequenceCounts.sum())
