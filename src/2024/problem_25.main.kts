#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val (lockLines, keyLines) = lines.chunked(8).map { it.take(7) }.partition { it[0][0] == '#' }

val locks = lockLines.map { lines -> (0..4).map { y -> lines.map { it[y] }.lastIndexOf('#') } }
val keys = keyLines.map { lines -> (0..4).map { y -> 5 - lines.map { it[y] }.lastIndexOf('.') } }

val total = keys.sumOf { key ->
    locks.count { lock -> (0..4).all { i -> key[i] + lock[i] <= 5 }}
}
println(total)
