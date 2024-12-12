#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0) 
}

val turnMap = (Direction.entries + listOf(Direction.NORTH)).zipWithNext().toMap()
data class Point(val x: Int, val y: Int)
data class Guard(val p: Point, val d: Direction) {
    fun move() = Guard(Point(p.x + d.x, p.y + d.y), d)
    fun turn() = Guard(p, turnMap.getValue(d))
}

fun walk(map: Map<Point, Char>, start: Guard): Set<Guard>? {
    var guard = start
    val seen = mutableSetOf<Guard>()
    while (true) {
        if (guard in seen) return null
        if (guard.p !in map) return seen
        seen += guard
        guard = guard.move().let { if (map[it.p] == '#') guard.turn() else it }
    }
}

val lines = java.io.File(args[0]).readLines()
val map = lines.flatMapIndexed { y, line -> line.mapIndexed { x, char -> Point(x, y) to char }}.toMap()
val start = Guard(map.keys.first { map[it] == '^' }, Direction.NORTH)
val seen = walk(map, start)?.map { it.p }?.toSet()
println(seen?.size)

fun Map<Point, Char>.withObstacle(p: Point) = toMutableMap().apply { put(p, '#') }
val loopCount = seen?.count { walk(map.withObstacle(it), start) == null }
println(loopCount)
