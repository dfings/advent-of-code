#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val (towels, patterns) = lines[0].split(", ") to lines.drop(2)

val cache = mutableMapOf<String, Long>("" to 1)
fun String.countSequences(): Long = cache.getOrPut(this) {
    towels.filter { startsWith(it) }.sumOf { removePrefix(it).countSequences() }
}

println(patterns.count { it.countSequences() > 0 })
println(patterns.sumOf { it.countSequences() })
