#!/usr/bin/env kotlin

import kotlin.math.min
import kotlin.math.max

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int) { NORTH(0, -1),  EAST(1, 0), SOUTH(0, 1), WEST(-1, 0) }
operator fun Point.plus(d: Direction) = Point(x + d.dx, y + d.dy)

val turnRight = (Direction.entries + Direction.NORTH).zipWithNext().toMap()
val turnLeft = (Direction.entries.reversed() + Direction.WEST).zipWithNext().toMap()

data class State(val p: Point, val d: Direction)

fun State.next(ultra: Boolean) = buildList {
    val l = turnLeft.getValue(d)
    val r = turnRight.getValue(d)
    var current = if (ultra) p + d + d + d else p
    repeat(if (ultra) 7 else 3) {
        current = current + d
        add(State(current, l))
        add(State(current, r))
    }
}

fun findShortestPath(costs: List<List<Int>>, ultra: Boolean): Int {
    val start1 = State(Point(0, 0), Direction.EAST)
    val start2 = State(Point(0, 0), Direction.SOUTH)
    val end = Point(costs[0].lastIndex, costs.lastIndex)
    val frontier = mutableSetOf(start1, start2)
    val minCost = mutableMapOf(start1 to 0, start2 to 0)
    val seen = mutableSetOf<State>()
    while (!frontier.isEmpty()) {
        val current = frontier.minBy { minCost.getValue(it) }
        val cost = minCost.getValue(current)
        frontier.remove(current)
        if (!seen.add(current)) continue
        if (current.p == end) return cost
        val (x1, y1) = current.p
        for (next in current.next(ultra)) {
            val (x2, y2) = next.p
            if (x2 in costs[0].indices && y2 in costs.indices) {
                val nextCost = cost + if (x1 == x2) {
                    (min(y1 + 1, y2)..max(y1 - 1, y2)).sumOf { costs[it][x1] }
                } else {
                    (min(x1 + 1, x2)..max(x1 - 1, x2)).sumOf { costs[y1][it] }
                }
                if (nextCost < minCost[next] ?: Int.MAX_VALUE) {
                    minCost[next] = nextCost
                    frontier.add(next)
                }
            }
        }
    }
    return -1
}


fun solve(lines: List<String>) {
    val costs = lines.map { it.map { "$it".toInt() } }
    println(findShortestPath(costs, ultra = false))
    println(findShortestPath(costs, ultra = true))
}

solve(java.io.File(args[0]).readLines())
