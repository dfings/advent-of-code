#!/usr/bin/env kotlin

import kotlin.math.max

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

data class Grid(val lastIndex: Int, val corrupted: Set<Point>) {
    val range = 0..lastIndex
    fun neighbors(p: Point) = Direction.entries.map { p + it }
        .filter { it.x in range && it.y in range && it !in corrupted }

    fun findShortestPathLength(): Int {
        val start = Point(0, 0)
        val end = Point(lastIndex, lastIndex)
        val minCost = mutableMapOf(start to 0)
        val frontier = mutableSetOf(start to 0)
        while (!frontier.isEmpty()) {
            val (point, cost) = frontier.minBy { it.second }
            frontier.remove(point to cost)
            if (point == end) return cost
            for (n in neighbors(point)) {
                val oldCost = minCost[n] ?: Int.MAX_VALUE
                val newCost = cost + 1
                if (newCost < oldCost) {  
                    minCost[n] = newCost
                    frontier.add(n to newCost)
                }
            }
        }
        return -1
    }
}

val lines = java.io.File(args[0]).readLines()
val corrupted = lines.map { it.split(",").let { Point(it[0].toInt(), it[1].toInt()) } }

val SIZE = 70
val memory1 = Grid(SIZE, corrupted.take(1024).toSet())
println(memory1.findShortestPathLength())

var count = corrupted.lastIndex / 2
var highestPass = 0
var lowestFail = Int.MAX_VALUE
for (diff in generateSequence(count / 2) { max(it / 2, 1) }) {
    val cost = Grid(SIZE, corrupted.take(count).toSet()).findShortestPathLength()
    if (cost == -1) {
        lowestFail = count
        count -= diff
    } else {
        highestPass = count
        count += diff
    }
    if (highestPass == lowestFail - 1) {
        val c = corrupted[count]
        println("${c.x},${c.y}")
        break
    }
}
