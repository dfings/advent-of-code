#!/usr/bin/env kotlin

val datastream = java.io.File(args[0]).readLines().single()

fun detect(windowSize: Int): Int {
    datastream.windowed(windowSize).forEachIndexed { i, value ->
        if (value.toSet().size == windowSize) return@detect i + windowSize
    }
    return -1
}

println(detect(4))
println(detect(14))
