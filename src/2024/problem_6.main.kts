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
    fun addObstacle(p: Point) = Grid(points.toMutableMap().apply { put(p, '#') })
    fun findGuard() = points.keys.first { get(it) == '^' }
}

class State(val grid: Grid) {
    var guard = Guard(grid.findGuard(), Dir.N)
    val seen = mutableSetOf<Guard>()
    var loop = false
    fun run() {
        while (guard !in seen && guard.p in grid.points) {
            seen += guard
            val next = guard.move()
            guard = if (grid.isObstacle(next.p)) guard.turn() else next
        }
        loop = guard.p in grid.points
    }
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(
    lines.flatMapIndexed { y, line -> line.mapIndexed { x, char -> Point(x, y) to char }}.toMap()
)
val state = State(grid)
state.run()
val points = state.seen.map { it.p }.toSet()
println(points.size)

var loopCount = 0
for (point in points - grid.findGuard()) {
    val newState = State(grid.addObstacle(point))
    newState.run()
    if (newState.loop) loopCount++
}
println(loopCount)
