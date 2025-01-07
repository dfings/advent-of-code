#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val dimensions = lines.map { it.split("x").map { it.toInt() }.sorted() }

println(dimensions.sumOf { (x, y, z) -> 3 * x * y + 2 * x * z + 2 * y * z })
println(dimensions.sumOf { (x, y, z) -> 2 * x + 2 * y + x * y * z })
