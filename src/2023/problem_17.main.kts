#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int) { NORTH(0, -1),  EAST(1, 0), SOUTH(0, 1), WEST(-1, 0) }
operator fun Point.plus(d: Direction) = Point(x + d.dx, y + d.dy)

val turnRight = (Direction.entries + Direction.NORTH).zipWithNext().toMap()
val turnLeft = (Direction.entries.reversed() + Direction.WEST).zipWithNext().toMap()

data class State(val p: Point, val d: Direction, val i: Int)

fun State.next() = buildList {
    if (i < 3) add(copy(p = p + d, i = i + 1))
    val l = turnLeft.getValue(d)
    add(State(p + l, l, 1))
    val r = turnRight.getValue(d)
    add(State(p + r, r, 1))
}

fun findShortestPath(costs: List<List<Int>>): Int {
    val start = State(Point(0, 0), Direction.EAST, 0)
    val end = Point(costs[0].lastIndex, costs.lastIndex)
    val frontier = mutableSetOf(start to 0)
    val seen = mutableSetOf<State>()
    while (!frontier.isEmpty()) {
        val (current, cost) = frontier.minBy { it.second }
        frontier.remove(current to cost)
        if (!seen.add(current)) continue
        if (current.p == end) return cost
        for (next in current.next()) {
            if (next.p.x in costs[0].indices && next.p.y in costs.indices) {
                frontier.add(next to (cost + costs[next.p.y][next.p.x]))
            }
        }
    }
    return -1
}


fun solve(lines: List<String>) {
    val costs = lines.map { it.map { "$it".toInt() } }
    println(findShortestPath(costs))
}

solve(java.io.File(args[0]).readLines())
