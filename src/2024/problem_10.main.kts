#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

class Grid(lines: List<String>) {
    val topographicMap = lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Point(x, y) to "$c".toInt() }
    }.toMap()
    
    fun height(p: Point) = topographicMap[p] ?: 0
    fun successors(p: Point) = 
        Direction.entries.map { p + it }.filter { height(it) == height(p) + 1  }

    fun reachable(p: Point): Set<Point> =
        if (height(p) == 9) setOf(p) else successors(p).flatMap { reachable(it) }.toSet()

    fun rating(p: Point): Int =
        if (height(p) == 9) 1 else successors(p).sumOf { rating(it) }
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(lines)
val trailheads = grid.topographicMap.filter { it.value == 0 }.keys
println(trailheads.sumOf { grid.reachable(it).size })
println(trailheads.sumOf { grid.rating(it) })
