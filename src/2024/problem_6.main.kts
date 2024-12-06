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

fun walk(map: Map<Point, Char>, guard: Guard): Set<Guard>? {
    var current = guard
    val seen = mutableSetOf<Guard>()
    while (true) {
        if (current in seen) return null
        if (current.p !in map) return seen
        seen += current
        val next = current.move()
        current = if (map[next.p] == '#') current.turn() else next
    }
}

val lines = java.io.File(args[0]).readLines()
val map = lines.flatMapIndexed { y, line -> line.mapIndexed { x, char -> Point(x, y) to char }}.toMap()
val guard = Guard(map.keys.first { map[it] == '^' }, Dir.N)
val seen = walk(map, guard)?.map { it.p }?.toSet()
println(seen?.size)

fun Map<Point, Char>.withObstacle(p: Point) = toMutableMap().apply { put(p, '#') }
val loopCount = seen?.count { walk(map.withObstacle(it), guard) == null }
println(loopCount)
