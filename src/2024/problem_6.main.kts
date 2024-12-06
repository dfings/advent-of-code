#!/usr/bin/env kotlin

enum class Dir(val x: Int, val y: Int) {
    N(0, -1), E(1, 0), S(0, 1), W(-1, 0)  // y increases S
}

data class Point(val x: Int, val y: Int) {
    fun move(d: Dir) = Point(x + d.x, y + d.y)
}

val turnMap = mapOf(Dir.N to Dir.E, Dir.E to Dir.S, Dir.S to Dir.W, Dir.W to Dir.N)
data class Guard(val p: Point, val d: Dir) {
    fun move() = Guard(p.move(d), d)
    fun turn() = Guard(p, turnMap.getValue(d))
}

class Grid(val points: Map<Point, Char>) {
    fun get(p: Point) = points[p] ?: '.'
    fun isObstacle(p: Point) = get(p) == '#'
    fun withObstacle(p: Point) = Grid(points.toMutableMap().apply { put(p, '#') })
}

fun walk(grid: Grid, guard: Guard): Set<Guard>? {
    var current = guard
    val seen = mutableSetOf<Guard>()
    while (true) {
        if (current in seen) return null
        if (current.p !in grid.points) return seen
        seen += current
        val next = current.move()
        current = if (grid.isObstacle(next.p)) current.turn() else next
    }
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(
    lines.flatMapIndexed { y, line -> line.mapIndexed { x, char -> Point(x, y) to char }}.toMap()
)
val guard = Guard(grid.points.keys.first { grid.get(it) == '^' }, Dir.N)
val points = walk(grid, guard)?.map { it.p }?.toSet()
println(points?.size)

val loopCount = points?.count { walk(grid.withObstacle(it), guard) == null }
println(loopCount)
