#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val towels = lines[0].split(", ")
val patterns = lines.drop(2)

val cache = mutableMapOf<String, Long>()
fun countTowelSequences(pattern: String): Long = when {
    pattern.isEmpty() -> 1
    else -> cache.getOrPut(pattern) {
        towels.filter { pattern.startsWith(it) }.sumOf { countTowelSequences(pattern.drop(it.length)) }
    }
}

val sequenceCounts = patterns.map { countTowelSequences(it) }
println(sequenceCounts.count {  it > 0 })
println(sequenceCounts.sum())
