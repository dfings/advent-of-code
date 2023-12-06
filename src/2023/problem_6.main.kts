#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun countWinners(time: Long, distance: Long) =
    (1..time).count { velocity -> distance < velocity * (time - velocity) }.toLong()

fun String.parse() = substringAfter(":").trim().split(Regex(" +")).map { it.toLong() }
val times = lines[0].parse()
val distances = lines[1].parse()
println(times.mapIndexed { i, time -> countWinners(time, distances[i]) }.reduce(Long::times))

val bigTime = times.map { it.toString() }.joinToString("").toLong()
val bigDistance = distances.map { it.toString() }.joinToString("").toLong()
println(countWinners(bigTime, bigDistance))
