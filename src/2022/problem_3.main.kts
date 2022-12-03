#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines().map { it.toList() }

fun Char.priority(): Int = when (this) {
    in 'a'..'z' -> this - 'a' + 1
    in 'A'..'Z' -> this - 'A' + 27
    else -> throw UnsupportedOperationException()
}

val priority1 = lines.sumBy { line ->
    val (first, second) = line.chunked(line.size / 2)
    (first intersect second).single().priority()
}

val priority2 = lines.windowed(3, 3).sumBy { (first, second, third) ->
    (first intersect second intersect third).single().priority()
}

println(priority1)
println(priority2)
