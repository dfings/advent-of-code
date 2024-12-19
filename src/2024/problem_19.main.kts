#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val towels = lines[0].split(", ")
val patterns = lines.drop(2)

val cache = mutableMapOf<String, Long>()
fun String.countSequences(): Long = when {
    isEmpty() -> 1
    else -> cache.getOrPut(this) {
        towels.filter { startsWith(it) }.sumOf { removePrefix(it).countSequences() }
    }
}

val sequenceCounts = patterns.map { it.countSequences() }
println(sequenceCounts.count {  it > 0 })
println(sequenceCounts.sum())
