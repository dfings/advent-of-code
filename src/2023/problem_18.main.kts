#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Long, val y: Long)
enum class Direction(val dx: Long, val dy: Long) { EAST(1, 0), SOUTH(0, 1), WEST(-1, 0), NORTH(0, -1) }
fun Point.move(d: Direction, distance: Long) = Point(x + d.dx * distance, y + d.dy * distance)

data class Instruction(val direction: Direction, val distance: Long)

val pattern = Regex("""(\w) (\d+) \(#(\w+)\)""")
val dirs = mapOf("U" to Direction.NORTH, "R" to Direction.EAST,
                 "D" to Direction.SOUTH, "L" to Direction.WEST)
fun parse(input: String): Pair<Instruction, Instruction> {
    val (a, b, c) = pattern.find(input)!!.destructured
    return Instruction(dirs.getValue(a), b.toLong()) to 
           Instruction(Direction.entries[c.takeLast(1).toInt()], c.dropLast(1).toLong(16))
}

val outer =(Direction.entries + Direction.EAST).zipWithNext().toMap()

fun List<Instruction>.getPoints() : List<Point> {
    val output = mutableListOf<Point>()
    var current = Point(0, 0)
    output.add(current)
    var lastOuter = true
    for (i in indices) {
        val (dir, len) = get(i)
        val turn = get((i + 1) % size).direction
        current = current.move(dir, len)
        val thisOuter = outer[dir] == turn
        if (lastOuter && thisOuter) current = current.move(dir, 1)
        if (!lastOuter && !thisOuter) current = current.move(dir, -1)
        lastOuter = thisOuter
        output.add(current)
    }
    return output
}

fun List<Point>.area() = zipWithNext().sumOf { (a, b) -> a.x * b.y - b.x * a.y } / 2L

fun solve(lines: List<String>) {
    val instructions = lines.map { parse(it) }
    println(instructions.map { it.first }.getPoints().area())
    println(instructions.map { it.second }.getPoints().area())
}

solve(java.io.File(args[0]).readLines())
