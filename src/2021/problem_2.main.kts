#!/usr/bin/env kotlin

data class Command(val direction: String, val step: Int)
val cmds = java.io.File(args[0])
    .readLines()
    .map { it.split(" ") }
    .map { Command(it[0], it[1].toInt())}

// Part 1
var position = 0
var depth = 0
cmds.forEach {
    when (it.direction) {
        "forward" -> position += it.step
        "up" -> depth -= it.step
        "down" -> depth += it.step
    }
}
println(position * depth)

// Part 2
position = 0
depth = 0
var aim = 0
cmds.forEach {
    when (it.direction) {
        "forward" -> {
            position += it.step
            depth += aim * it.step
        }
        "up" -> aim -= it.step
        "down" -> aim += it.step
    }
}
println(position * depth)
