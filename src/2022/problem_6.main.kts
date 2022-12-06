#!/usr/bin/env kotlin

val datastream = java.io.File(args[0]).readLines().single()

fun String.detect(windowSize: Int): Int = 
    windowed(windowSize).indexOfFirst { it.toSet().size == windowSize } + windowSize

println(datastream.detect(4))
println(datastream.detect(14))
