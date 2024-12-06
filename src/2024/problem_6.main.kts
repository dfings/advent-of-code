#!/usr/bin/env kotlin

enum class Dir(val x: Int, val y: Int) {
    N(0, -1), E(1, 0), S(0, 1), W(-1, 0)  // y increases S
}

val turnMap = mapOf(Dir.N to Dir.E, Dir.E to Dir.S, Dir.S to Dir.W, Dir.W to Dir.N)
data class Point(val x: Int, val y: Int)
data class Guard(val p: Point, val d: Dir) {
    fun move() = Guard(Point(p.x + d.x, p.y + d.y), d)
    fun turn() = Guard(p, turnMap.getValue(d))
}

class Grid(val points: Map<Point, Char>) {
    fun get(p: Point) = points[p] ?: '.'
}

fun walk(grid: Grid, guard: Guard): Set<Guard>? {
    var current = guard
    val seen = mutableSetOf<Guard>()
    while (true) {
        if (current in seen) return null
        if (current.p !in grid.points) return seen
        seen += current
        val next = current.move()
        current = if (grid.get(next.p) == '#') current.turn() else next
    }
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(
    lines.flatMapIndexed { y, line -> line.mapIndexed { x, char -> Point(x, y) to char }}.toMap()
)
val guard = Guard(grid.points.keys.first { grid.get(it) == '^' }, Dir.N)
val points = walk(grid, guard)?.map { it.p }?.toSet()
println(points?.size)

fun Grid.withObstacle(p: Point) = Grid(points.toMutableMap().apply { put(p, '#') })
val loopCount = points?.count { walk(grid.withObstacle(it), guard) == null }
println(loopCount)
