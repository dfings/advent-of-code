#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val values = lines.map { it.split("-", ",").map { it.toInt() } }

val part1 = values.count { (a, b, c, d) -> (a <= c && b >= d) || (a >= c && b <= d) }
val part2 = values.count { (a, b, c, d) -> a <= d && b >= c }

println(part1)
println(part2)
