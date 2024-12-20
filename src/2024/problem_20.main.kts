#!/usr/bin/env kotlin

import kotlin.math.abs

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

data class Track(val start: Point, val walls: Set<Point>, val end: Point) {
    fun Point.neighbors() = Direction.entries.map { this + it }.filter { it !in walls }

    fun findMinCosts(): Map<Point, Int> {
        val minCost = mutableMapOf(start to 0)
        val frontier = mutableSetOf(start to 0)
        while (!frontier.isEmpty()) {
            val (point, cost) = frontier.minBy { it.second }
            frontier.remove(point to cost)
            for (n in point.neighbors()) {
                val oldCost = minCost[n] ?: Int.MAX_VALUE
                val newCost = cost + 1
                if (newCost < oldCost) {  
                    minCost[n] = newCost
                    frontier.add(n to newCost)
                }
            }
        }
        return minCost
    }
}

fun analyzeCheats(costs: Map<Point, Int>, cheats: Int, target: Int): Int {
    val effectiveCheats = mutableMapOf<Pair<Point, Point>, Int>()
    for ((point, cost) in costs.entries) {
        for (yDelta in -cheats..cheats) {
            for (xDelta in -cheats..cheats) {
                val cheatCost = abs(xDelta) + abs(yDelta)
                val p = Point(point.x + xDelta, point.y + yDelta)
                if (cheatCost <= cheats && p in costs) {
                    val savings = costs.getValue(p) - costs.getValue(point) - cheatCost
                    if (savings >= target) {
                        effectiveCheats.put(point to p, savings)
                    }
                }
            }
        }
    }
    return effectiveCheats.size
}


fun parseTrack(lines: List<String>): Track {
    val pointToChar = lines.flatMapIndexed { y, line -> 
        line.mapIndexed { x, it -> Point(x, y) to it }
    }
    return Track(
        pointToChar.single { it.second == 'S'}.first,
        pointToChar.filter { it.second == '#' }.map { it.first }.toSet(),
        pointToChar.single { it.second == 'E'}.first,
    )
}

val lines = java.io.File(args[0]).readLines()
val track = parseTrack(lines)
val minCosts = track.findMinCosts()
println(analyzeCheats(minCosts, 2, 100))
println(analyzeCheats(minCosts, 20, 100))
