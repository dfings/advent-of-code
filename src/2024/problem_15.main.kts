#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}
val directions = mapOf('^' to Direction.NORTH, 'v' to Direction.SOUTH,
                       '>' to Direction.EAST, '<' to Direction.WEST)
fun Char.toDirection() = directions.getValue(this)

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)
fun Point.score() = 100 * y + x
fun Point.score2() = 100 * y + x

data class Warehouse(val robot: Point, val boxes: Set<Point>, val walls: Set<Point>) {
    fun next(d: Direction): Warehouse {
        val newRobot = robot + d
        val newBoxes = mutableSetOf<Point>()
        val oldBoxes = mutableSetOf<Point>()
        var force = newRobot
        while (true) {
            when {
                force in walls -> return this
                force in boxes -> {
                    oldBoxes.add(force)
                    force = force + d
                    newBoxes.add(force)
                }
                else -> break
            }
        }
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
        if (d == Direction.NORTH || d == Direction.SOUTH) {
            var force = setOf(newRobot)
            while (true) {
                when {
                    force.any { it in walls } -> return this
                    force.any { it in lBoxes || it in rBoxes } -> {                    
                        force += force.filter { it in rBoxes }.map { it + Direction.WEST }
                        force += force.filter { it in lBoxes }.map { it + Direction.EAST }
                        oldLBoxes += force.filter { it in lBoxes}
                        oldRBoxes += force.filter { it in rBoxes}
                        force = force.map { it + d }.filter { it in lBoxes || it in rBoxes || it in walls }.toSet()
                        newLBoxes += oldLBoxes.map { it + d }
                        newRBoxes += oldRBoxes.map { it + d}
                    }
                    else -> break
                }
            }
        } else {
            var force = newRobot
            while (true) {
                when {
                    force in walls -> return this
                    force in lBoxes -> {
                        oldLBoxes.add(force)
                        force = force + d
                        newLBoxes.add(force)
                    }
                    force in rBoxes -> {
                        oldRBoxes.add(force)
                        force = force + d
                        newRBoxes.add(force)
                    }
                    else -> break
                }
            }
        }
        return WideWarehouse(newRobot, (lBoxes - oldLBoxes) + newLBoxes, (rBoxes - oldRBoxes) + newRBoxes, walls)
    }
}
fun parseWideWarehouse(lines: List<String>): WideWarehouse {
    var robot: Point? = null
    val lBoxes = mutableSetOf<Point>()
    val rBoxes = mutableSetOf<Point>()
    val walls = mutableSetOf<Point>()
    for (y in 0..lines.lastIndex) {
        for (x in 0..lines[y].lastIndex) {
            when (lines[y][x]) {
                '@' -> robot = Point(2 * x, y)
                'O' -> { 
                    lBoxes.add(Point(2 * x, y))
                    rBoxes.add(Point(2 * x + 1, y))
                }
                '#' -> {
                    walls.add(Point(2 * x, y))
                    walls.add(Point(2 * x + 1, y))
                }
            }
        }
    }
    return WideWarehouse(robot!!, lBoxes, rBoxes, walls)
}

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

val lines = java.io.File(args[0]).readLines()
val warehouseLines = lines.takeWhile { it != "" }
var warehouse = parseWarehouse(warehouseLines)
val instructions = lines.drop(warehouseLines.size + 1).joinToString("").map { it.toDirection() }

for (d in instructions) {
    warehouse = warehouse.next(d)
}
println(warehouse.boxes.sumOf { it.score() })


var warehouse2 = parseWideWarehouse(warehouseLines)
for (d in instructions) {
    warehouse2 = warehouse2.next(d)
}
println(warehouse2.lBoxes.sumOf { it.score2() })