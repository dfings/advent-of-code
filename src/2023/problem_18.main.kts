#!/usr/bin/env kotlin

data class Point(val x: Long, val y: Long)
enum class Direction(val dx: Long, val dy: Long) { EAST(1, 0), SOUTH(0, 1), WEST(-1, 0), NORTH(0, -1) }
data class Instruction(val direction: Direction, val distance: Long)
operator fun Point.plus(i: Instruction) = Point(x + i.direction.dx * i.distance, y + i.direction.dy * i.distance)

val pattern = Regex("""(\w) (\d+) \(#(\w+)\)""")
val dirs = mapOf("U" to Direction.NORTH, "R" to Direction.EAST,
                 "D" to Direction.SOUTH, "L" to Direction.WEST)
fun parse(input: String): Pair<Instruction, Instruction> {
    val (a, b, c) = pattern.find(input)!!.destructured
    return Instruction(dirs.getValue(a), b.toLong()) to 
           Instruction(Direction.entries[c.takeLast(1).toInt()], c.dropLast(1).toLong(16))
}

// Pick's theorem: A = i + b/2 - 1 => A + b/2 + 1 = i + b
fun List<Instruction>.getPoints() = runningFold(Point(0, 0)) { acc, it -> acc + it }
fun List<Instruction>.area() = getPoints().zipWithNext().sumOf { (a, b) -> a.x * b.y - b.x * a.y } / 2L
fun List<Instruction>.boundaryPoints() = sumOf { it.distance }
fun List<Instruction>.interiorAndBoundaryPoints() = area() + boundaryPoints() / 2 + 1

fun solve(lines: List<String>) {
    val instructions = lines.map { parse(it) }
    println(instructions.map { it.first }.interiorAndBoundaryPoints())
    println(instructions.map { it.second }.interiorAndBoundaryPoints())
}

solve(java.io.File(args[0]).readLines())
