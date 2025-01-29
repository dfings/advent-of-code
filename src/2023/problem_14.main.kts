#!/usr/bin/env kotlin

enum class Direction { NORTH, WEST, SOUTH, EAST }

val minus = setOf(Direction.NORTH, Direction.WEST)
val tiltX = setOf(Direction.WEST, Direction.EAST)
fun tilt(lines: List<String>, direction: Direction): List<String> {
    val output = lines.map{ CharArray(it.length) { '.' } }
    for (scan in lines.indices) {
        var rock = if (direction in minus) -1 else lines.size
        val range = if (direction in minus) lines.indices else lines.indices.reversed()
        for (move in range) {
            val x = if (direction in tiltX) move else scan
            val y = if (direction in tiltX) scan else move
            when (lines[y][x]) {
                '#' -> { 
                    rock = move
                    output[y][x] = '#'
                }
                'O' -> {
                    rock += if (direction in minus) 1 else -1
                    if (direction in tiltX) {
                        output[y][rock] = 'O'
                    } else {
                        output[rock][x] = 'O'
                    }
                }
            }
        }
    }
    return output.map { it.joinToString("") }
}

fun List<String>.load() = withIndex().sumOf { (y, line) -> line.count { it == 'O' } * (size - y) }

fun solve(lines: List<String>) {
    println(tilt(lines, Direction.NORTH).load())
    val cache = mutableMapOf(lines to 0)
    var state = lines
    var index = 1
    while (true) {
        for (direction in Direction.entries) {
            state = tilt(state, direction)
        }
        val start = cache.put(state, index++)
        if (start != null) {
            val cycleLength = (index - start - 1).toLong()
            val targetIndex = start + (1000000000L - start).mod(cycleLength).toInt()
            val targetState = cache.entries.single { it.value == targetIndex }.key
            println(targetState.load())
            return
        }
    }
}

solve(java.io.File(args[0]).readLines())
