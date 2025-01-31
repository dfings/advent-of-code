#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int) { NORTH(0, -1),  EAST(1, 0), SOUTH(0, 1), WEST(-1, 0) }
operator fun Point.plus(d: Direction) = Point(x + d.dx, y + d.dy)

data class Instruction(val direction: Direction, val distance: Int, val color: String)

val pattern = Regex("""(\w) (\d+) \(#(\w+)\)""")
val dirs = mapOf("U" to Direction.NORTH, "R" to Direction.EAST,
                 "D" to Direction.SOUTH, "L" to Direction.WEST)
fun parse(input: String): Instruction {
    val (a, b, c) = pattern.find(input)!!.destructured
    return Instruction(dirs.getValue(a), b.toInt(), c)
}

fun solve(lines: List<String>) {
    val instructions = lines.map { parse(it) }
    var current = Point(0, 0)
    val filled = mutableSetOf(current)
    for (instruction in instructions) {
        repeat(instruction.distance) {
            current = current + instruction.direction
            filled.add(current)
        }
    }
    val minPoint = filled.minBy { it.x + it.y }
    check(Point(minPoint.x + 1, minPoint.y + 1) !in filled)
    val frontier = ArrayDeque(listOf(Point(minPoint.x + 1, minPoint.y + 1)))
    while (!frontier.isEmpty()) {
        val current = frontier.removeFirst()
        if (!filled.add(current)) continue
        frontier.addAll(Direction.entries.map { current + it }.filter { it != filled })
    }

    println(filled.size)
}

solve(java.io.File(args[0]).readLines())
