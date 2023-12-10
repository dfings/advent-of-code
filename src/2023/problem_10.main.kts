#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

enum class Dir(val dx: Int, val dy: Int) { NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0) }
val nextDir = mapOf(
    Dir.NORTH to mapOf('|' to Dir.NORTH, '7' to Dir.WEST, 'F' to Dir.EAST),
    Dir.SOUTH to mapOf('|' to Dir.SOUTH, 'J' to Dir.WEST, 'L' to Dir.EAST),
    Dir.EAST to mapOf('-' to Dir.EAST, 'J' to Dir.NORTH, '7' to Dir.SOUTH),
    Dir.WEST to mapOf('-' to Dir.WEST, 'L' to Dir.NORTH, 'F' to Dir.SOUTH),
)

data class Point(val x: Int, val y: Int) {
    fun move(d: Dir) = Point(x + d.dx, y + d.dy)
    fun isValid() = x >= 0 && x < lines[0].length && y >= 0 && y < lines.size
    fun isValid(d: Dir) = move(d).let { it.isValid() &&it.symbol() in nextDir.getValue(d).keys }
    fun symbol() = lines[y][x]
}

val (y, line) = lines.withIndex().find { it.value.any { it == 'S' } }!!
val animal = Point(line.indexOf('S'), y)

var lastMove = Dir.values().first { animal.isValid(it) }
var current = animal.move(lastMove)
var step = 1
while (current != animal) {
    lastMove = nextDir.getValue(lastMove).getValue(current.symbol())
    current = current.move(lastMove)
    step++
}

println(step / 2)
