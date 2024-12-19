#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val (patterns, designs) = lines[0].split(", ") to lines.drop(2)

val cache = mutableMapOf<String, Long>("" to 1)
fun String.countSequences(): Long = cache.getOrPut(this) {
    patterns.filter { startsWith(it) }.sumOf { removePrefix(it).countSequences() }
}

println(designs.count { it.countSequences() > 0 })
println(designs.sumOf { it.countSequences() })
