#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun countWinners(time: Long, distance: Long) =
    (1..<time).count { velocity -> distance < velocity * (time - velocity) }.toLong()

fun String.parse() = substringAfter(":").trim().split(Regex(" +")).map { it.toLong() }
val (times, distances) = lines.map { it.parse() }
println(times.mapIndexed { i, time -> countWinners(time, distances[i]) }.reduce(Long::times))

val bigTime = times.joinToString("").toLong()
val bigDistance = distances.joinToString("").toLong()
println(countWinners(bigTime, bigDistance))
