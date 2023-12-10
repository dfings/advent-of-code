#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Point(val x: Int, val y: Int)
enum class Dir(val dx: Int, val dy: Int) { NORTH(0, -1), SOUTH(0, 1), EAST(1, 0), WEST(-1, 0) }

val nextDir = mapOf(
    Dir.NORTH to mapOf('|' to Dir.NORTH, '7' to Dir.WEST, 'F' to Dir.EAST),
    Dir.SOUTH to mapOf('|' to Dir.SOUTH, 'J' to Dir.WEST, 'L' to Dir.EAST),
    Dir.EAST to mapOf('-' to Dir.EAST, 'J' to Dir.NORTH, '7' to Dir.SOUTH),
    Dir.WEST to mapOf('-' to Dir.WEST, 'L' to Dir.NORTH, 'F' to Dir.SOUTH),
)

val yMax = lines.lastIndex
val xMax = lines[0].lastIndex
 
fun Point.symbol() = lines[y][x]
fun Point.move(d: Dir) = Point(x + d.dx, y + d.dy)
fun Point.isValid() = x >= 0 && x <= xMax && y >= 0 && y <= yMax
fun Point.isValid(d: Dir) = move(d).let { it.isValid() && it.symbol() in nextDir.getValue(d).keys }

val (y, line) = lines.withIndex().find { it.value.any { it == 'S' } }!!
val animal = Point(line.indexOf('S'), y)

var lastMove = Dir.values().first { animal.isValid(it) }
var current = animal.move(lastMove)
val path = mutableListOf(current)
while (current != animal) {
    lastMove = nextDir.getValue(lastMove).getValue(current.symbol())
    current = current.move(lastMove)
    path.add(current)
}
println(path.size / 2)

val projectedPath = path.flatMap {
    val p = Point(2 * it.x, 2 * it.y)
    when (it.symbol()) {
        '|', '7' -> listOf(p, p.move(Dir.SOUTH))
        '-', 'L' -> listOf(p, p.move(Dir.EAST))
        'F', 'S' -> listOf(p, p.move(Dir.SOUTH), p.move(Dir.EAST))
        else -> listOf(p)
    }
}.toSet()

fun Point.isValidProjected() = x >= -1 && y >= -1 && x <= 2 * (xMax + 1) &&  y <= 2 * (yMax + 1)
fun Point.neighbors() = Dir.values().map { move(it) }.filter { it.isValidProjected() }

val seen = mutableSetOf(Point(-1, -1))
val frontier = ArrayDeque<Point>(Point(-1, -1).neighbors().filter { it !in projectedPath })
while (!frontier.isEmpty()) {
    val point = frontier.removeFirst()
    for (next in point.neighbors().filter{ it !in projectedPath }) {
        if (seen.add(next)) frontier.add(next)
    }
}

fun Point.isInterior() = Point(2 * x, 2 * y).let { it !in seen && it !in projectedPath }
var count = (0..xMax).sumOf { x -> (0..yMax).map { y -> if (Point(x, y).isInterior()) 1 else 0 }.sum() }
println(count)
