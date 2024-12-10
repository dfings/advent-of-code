#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)

class Grid(lines: List<String>) {
    val topographicMap =  lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Point(x, y) to "$c".toInt() }
    }.toMap()
    
    fun height(p: Point) = topographicMap[p] ?: 0
    fun successors(p: Point) = 
        listOf(p.copy(x = p.x + 1), p.copy(x = p.x - 1),
               p.copy(y = p.y + 1), p.copy(y = p.y - 1))
           .filter { height(it) == height(p) + 1  }

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
