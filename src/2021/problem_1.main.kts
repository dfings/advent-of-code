#!/usr/bin/env kotlin

val data = java.io.File(args[0]).readLines().map { it.toInt() }
println(data.windowed(2).count { it[0] < it[1] })
println(data.windowed(4).count { it.dropLast(1).sum() < it.drop(1).sum() })
