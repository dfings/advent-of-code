#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines().map { it.toList() }

fun Char.priority(): Int = when (this) {
    in 'a'..'z' -> this - 'a' + 1
    in 'A'..'Z' -> this - 'A' + 27
    else -> throw UnsupportedOperationException()
}

fun Iterable<List<Char>>.priority(): Int = reduce { a, b -> (a intersect b).toList() }.single().priority()

println(lines.sumBy { it.chunked(it.size / 2).priority() })
println(lines.chunked(3).sumBy { it.priority() })
