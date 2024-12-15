#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}
val directions = mapOf('^' to Direction.NORTH, 'v' to Direction.SOUTH,
                       '>' to Direction.EAST, '<' to Direction.WEST)
fun Char.toDirection() = directions.getValue(this)

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

typealias BigBox = Pair<Point, Point>
data class WideWarehouse(val robot: Point, val boxes: Set<BigBox>, val walls: Set<Point>) {
    val boxPoints = boxes.flatMap { listOf(it.first to it, it.second to it) }.toMap()
    fun next(d: Direction): WideWarehouse {
        val newRobot = robot + d
        val newBoxes = mutableSetOf<BigBox>()
        val oldBoxes = mutableSetOf<BigBox>()
        var force = setOf(newRobot)
        while (force.all { it in boxPoints } && !force.isEmpty()) {
            if (d == Direction.NORTH || d == Direction.SOUTH) {
                force += force.flatMap { boxPoints.getValue(it).toList() }
                oldBoxes += force.map { boxPoints.getValue(it) }
                force = force.map { it + d }.filter { it in boxPoints || it in walls }.toSet()
                newBoxes += oldBoxes.map { it.first + d to it.second + d }
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

fun Warehouse.makeWide() = WideWarehouse(
    Point(2 * robot.x, robot.y),
    boxes.map { Point(2 * it.x, it.y) to Point(2 * it.x + 1, it.y) }.toSet(),
    walls.flatMap { listOf(Point(2 * it.x, it.y), Point(2 * it.x + 1, it.y))}.toSet(),
)

val lines = java.io.File(args[0]).readLines()
val warehouseLines = lines.takeWhile { it != "" }
val warehouse = parseWarehouse(warehouseLines)
val instructions = lines.drop(warehouseLines.size + 1).joinToString("").map { it.toDirection() }

var w = warehouse
for (d in instructions) {
    w = w.next(d)
}
println(w.boxes.sumOf { 100 * it.y + it.x })

var w2 = warehouse.makeWide()
for (d in instructions) {
    w2 = w2.next(d)
}
println(w2.boxes.sumOf { 100 * it.first.y + it.first.x })
