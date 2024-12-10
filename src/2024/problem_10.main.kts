#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)

class Grid(data: List<String>) {
    val xRange = 0..data[0].lastIndex
    val yRange = 0..data.lastIndex
    val topographicMap =  xRange.flatMap { x ->
        yRange.map { y -> Point(x, y) to "${lines[y][x]}".toInt() }
    }.toMap()
    
    fun height(p: Point) = topographicMap[p] ?: 0
    fun successors(p: Point) = 
        listOf(p.copy(x = p.x + 1), p.copy(x = p.x - 1),
               p.copy(y = p.y + 1), p.copy(y = p.y - 1))
           .filter { height(it) == height(p) + 1  }

    private val reachableCache = mutableMapOf<Point, Set<Point>>()
    fun reachable(p: Point): Set<Point> = reachableCache.getOrPut(p) {
        if (height(p) == 9) setOf(p) else successors(p).flatMap { reachable(it) }.toSet()
    }

    val ratingCache = mutableMapOf<Point, Int>()
    fun rating(p: Point): Int = ratingCache.getOrPut(p) {
        if (height(p) == 9) 1 else successors(p).sumOf { rating(it) }
    }
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(lines)
val trailheads = grid.topographicMap.filter { it.value == 0 }.keys
println(trailheads.sumOf { grid.reachable(it).size })
println(trailheads.sumOf { grid.rating(it) })

