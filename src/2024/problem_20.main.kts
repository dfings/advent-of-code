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

    fun analyzeCheats(pathCosts: Map<Point, Int>, cheats: Int, target: Int): Int {
        val pathLength = pathCosts.getValue(end)
        val effectiveCheats = mutableMapOf<Pair<Point, Point>, Int>()
        for ((cheatStart, cheatStartCost) in pathCosts.entries) {
            if (pathLength - cheatStartCost < target) continue
            for (yDelta in -cheats..cheats) {
                val absYDelta = abs(yDelta)
                for (xDelta in (-cheats + absYDelta)..(cheats - absYDelta)) {
                    val cheatEnd = Point(cheatStart.x + xDelta, cheatStart.y + yDelta)
                    val cheatEndCost = pathCosts[cheatEnd]
                    if (cheatEndCost == null) continue
                    
                    val cheatCost = abs(xDelta) + absYDelta
                    val savings = cheatEndCost - cheatStartCost - cheatCost
                    if (savings < target) continue
                            
                    effectiveCheats.put(cheatStart to cheatEnd, savings)
                }
            }
        }
        return effectiveCheats.size
    }
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
println(track.analyzeCheats(minCosts, 2, 100))
println(track.analyzeCheats(minCosts, 20, 100))
