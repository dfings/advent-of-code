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

data class WideWarehouse(val robot: Point, val lBoxes: Set<Point>, val rBoxes: Set<Point>, val walls: Set<Point>) {
    fun next(d: Direction): WideWarehouse {
        val newRobot = robot + d
        val newLBoxes = mutableSetOf<Point>()
        val newRBoxes = mutableSetOf<Point>()
        val oldLBoxes = mutableSetOf<Point>()
        val oldRBoxes = mutableSetOf<Point>()
        var force = setOf(newRobot)
        while (force.all { it in lBoxes || it in rBoxes} && !force.isEmpty()) {
            if (d == Direction.NORTH || d == Direction.SOUTH) {
                force += force.filter { it in rBoxes }.map { it + Direction.WEST }
                force += force.filter { it in lBoxes }.map { it + Direction.EAST }
                oldLBoxes += force.filter { it in lBoxes}
                oldRBoxes += force.filter { it in rBoxes}
                force = force.map { it + d }.filter { it in lBoxes || it in rBoxes || it in walls }.toSet()
                newLBoxes += oldLBoxes.map { it + d }
                newRBoxes += oldRBoxes.map { it + d }
            } else {
                val f = force.single()
                if (f in lBoxes) {
                    oldLBoxes += f
                    force = setOf(f + d)
                    newLBoxes += f + d
                } else {
                    oldRBoxes += f
                    force = setOf(f + d)
                    newRBoxes += f + d
                }
            }
        }
        if (force.any { it in walls }) return this
        return WideWarehouse(newRobot, (lBoxes - oldLBoxes) + newLBoxes, (rBoxes - oldRBoxes) + newRBoxes, walls)
    }
}

fun Warehouse.makeWide() = WideWarehouse(
    Point(2 * robot.x, robot.y),
    boxes.map { Point(2 * it.x, it.y) }.toSet(),
    boxes.map { Point(2 * it.x + 1, it.y) }.toSet(),
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
println(w2.lBoxes.sumOf { 100 * it.y + it.x })

// For debugging.
fun WideWarehouse.print() {
    val maxPoint = walls.maxBy { it.x * it.y }
    for (y in 0..maxPoint.y) {
        val line = mutableListOf<String>()
        for (x in 0..maxPoint.x) {
            val p = Point(x, y)
            line += when {
                p == robot -> "@"
                p in lBoxes -> "["
                p in rBoxes -> "]"
                p in walls -> "#"
                else -> "."
            }
        }
        println(line.joinToString(""))
    }
}
