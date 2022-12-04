#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val values = lines.map { it.split("-", ",").map { it.toInt() } }

val part1 = values.filter { (a, b, c, d) -> (a <= c && b >= d) || (a >= c && b <= d) }.count()
val part2 = values.filter { (a, b, c, d) -> (a <= c && b >= c) || (a >= c && a <= d) }.count()

println(part1)
println(part2)
