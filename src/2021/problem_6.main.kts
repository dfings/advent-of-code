#!/usr/bin/env kotlin

val spawn = listOf(6, 8)
val fishStart = java.io.File(args[0]).readLines().first().split(",").map { it.toInt() }
val fishEnd = generateSequence(0 to fishStart) { (i, fish) ->
    if (i == 80) null else i + 1 to fish.flatMap { if (it > 0) listOf(it - 1) else spawn }
}.last().second
println(fishEnd.size)
