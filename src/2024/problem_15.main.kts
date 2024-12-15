#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

data class Warehouse(val robot: Point, val boxes: Set<Point>, val walls: Set<Point>) {
    fun next(d: Direction): Warehouse {
        val newRobot = robot + d
        val newBoxes = mutableSetOf<Point>()
        val oldBoxes = mutableSetOf<Point>()
        var force = newRobot
        while (force in boxes) {
            oldBoxes.add(force)
            newBoxes.add(force + d)
            force = force + d
        }
        if (force in walls) return this
        return Warehouse(newRobot, (boxes - oldBoxes) + newBoxes, walls)
    }
}

typealias BigBox = Pair<Point, Point>
data class WideWarehouse(val robot: Point, val boxes: Set<BigBox>, val walls: Set<Point>) {
    val boxPoints = boxes.flatMap { listOf(it.first to it, it.second to it) }.toMap()
    fun next(d: Direction): WideWarehouse {
        val newRobot = robot + d
        val newBoxes = mutableSetOf<BigBox>()
        val oldBoxes = mutableSetOf<BigBox>()
        var force = setOf(newRobot)
        while (force.all { it in boxPoints } && force.isNotEmpty()) {
            if (d == Direction.NORTH || d == Direction.SOUTH) {
                force += force.flatMap { boxPoints.getValue(it).toList() }
                val boxes = force.map { boxPoints.getValue(it) }
                oldBoxes += boxes
                newBoxes += boxes.map { it.first + d to it.second + d }
                force = force.map { it + d }.filter { it in boxPoints || it in walls }.toSet()
            } else {
                val f = force.single()
                val box = boxPoints.getValue(f)
                oldBoxes += box
                newBoxes += box.first + d to box.second + d
                force = setOf(f + d + d)
            }
        }
        if (force.any { it in walls }) return this
        return WideWarehouse(newRobot, (boxes - oldBoxes) + newBoxes, walls)
    }
}

fun parseWarehouse(lines: List<String>): Warehouse {
    var robot: Point? = null
    val boxes = mutableSetOf<Point>()
    val walls = mutableSetOf<Point>()
    for (y in 0..lines.lastIndex) {
        for (x in 0..lines[y].lastIndex) {
            when (lines[y][x]) {
                '@' -> robot = Point(x, y)
                'O' -> boxes.add(Point(x, y))
                '#' -> walls.add(Point(x, y))
            }
        }
    }
    return Warehouse(robot!!, boxes, walls)
}

fun Warehouse.wide() = WideWarehouse(
    Point(2 * robot.x, robot.y),
    boxes.map { Point(2 * it.x, it.y) to Point(2 * it.x + 1, it.y) }.toSet(),
    walls.flatMap { listOf(Point(2 * it.x, it.y), Point(2 * it.x + 1, it.y))}.toSet(),
)

val lines = java.io.File(args[0]).readLines()
val warehouseLines = lines.takeWhile { it != "" }
val warehouseSize = warehouseLines.size
val warehouse = parseWarehouse(warehouseLines)
val directions = mapOf('^' to Direction.NORTH, 'v' to Direction.SOUTH,
                       '>' to Direction.EAST, '<' to Direction.WEST)
val instructions = lines.drop(warehouseSize + 1).joinToString("").map { directions.getValue(it) }

fun Iterable<Point>.score() = sumOf { 100 * it.y + it.x }
println(instructions.fold(warehouse) { w, d -> w.next(d) }.boxes.score())
println(instructions.fold(warehouse.wide()) { w, d -> w.next(d) }.boxes.map { it.first }.score())
