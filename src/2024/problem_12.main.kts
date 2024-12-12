#!/usr/bin/env kotlin

enum class Dir(val x: Int, val y: Int) {
    N(0, -1), E(1, 0), S(0, 1), W(-1, 0)  // y increases S
}

data class Point(val x: Int, val y: Int) 
operator fun Point.plus(d: Dir) = Point(x + d.x, y + d.y)

class Grid(lines: List<String>) {
    val farm = lines.flatMapIndexed { y, line ->
        line.mapIndexed { x, c -> Point(x, y) to c }
    }.toMap()

    fun neighbors(p: Point) = Dir.entries.map { p + it }.filter { farm[p] == farm[it]  }

    fun region(p: Point): Set<Point> {
        val frontier = ArrayDeque<Point>(listOf(p))
        val region = mutableSetOf<Point>()
        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (region.add(current)) {
                frontier.addAll(neighbors(current))
            }
        }
        return region
    }

    fun regions(): List<Set<Point>> = buildList {
        for (p in farm.keys) {
            if (none { p in it }) {
                add(region(p))
            }
        }
    }

    fun perimeter(region: Set<Point>) = region.sumOf { 4 - neighbors(it).size }

    fun corners(p: Point): Int {
        var count = 0
        val value = farm[p] 
        for ((d1, d2) in (Dir.entries + listOf(Dir.N)).zipWithNext()) {
            val adjacent1 = farm[p + d1]
            val adjacent2 = farm[p + d2]
            val diagonal = farm[p + d1 + d2]
            if (value != adjacent1 && value != adjacent2) count++
            if (value == adjacent1 && value == adjacent2 && value != diagonal) count++
        }
        return count
    }
    
    fun corners(region: Set<Point>) = region.sumOf { sides(it) }
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(lines)
val regions = grid.regions()

println(regions.sumOf { it.size * grid.perimeter(it) })
println(regions.sumOf { it.size * grid.corners(it)  })
