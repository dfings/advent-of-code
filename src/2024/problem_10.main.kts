#!/usr/bin/env kotlin

enum class Dir(val x: Int, val y: Int) {
    N(0, -1), E(1, 0), S(0, 1), W(-1, 0)  // y increases S
}

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Dir) = Point(x + d.x, y + d.y)

class Grid(lines: List<String>) {
    val topographicMap = lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Point(x, y) to "$c".toInt() }
    }.toMap()
    
    fun height(p: Point) = topographicMap[p] ?: 0
    fun successors(p: Point) = Dir.entries.map { p + it }.filter { height(it) == height(p) + 1  }

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
