#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
fun Point.split() = listOf(copy(x = x - 1), copy(x = x + 1))

class GameState(val start: Point, val splitters: Set<Point>) {
    val yMax = splitters.maxOf { it.y } + 1
    var current = setOf<Point>(start)
    val paths = mutableMapOf(start to 1L)

    fun step(): Boolean {
        if (current.first().y == yMax) return false
        val next = mutableSetOf<Point>()
        for (p in current) {
            val n = p.copy(y = p.y + 1)
            next.add(n)
            link(n, p)
            if (n in splitters) {
                next.remove(n)
                for (s in n.split()) {
                    next.add(s)
                    link(s, p)
                }
            }
        }
        current = next
        return true
    }

    fun link(next: Point, prev: Point) {
        paths[next] = paths.getOrDefault(next, 0L) + paths.getValue(prev)
    }
}

fun solve(input: List<String>) {
    val start = Point(input[0].indexOf('S'), 0)
    val splitters = input.flatMapIndexed { y, line -> 
        line.mapIndexedNotNull { x, it -> if (it == '^') Point(x, y) else null } 
    }.toSet()
    val gameState = GameState(start, splitters)
    while (gameState.step()) {}
    println(gameState.splitters.count { it in gameState.paths.keys })
    println(gameState.paths.entries.filter { it.key.y == gameState.yMax }.sumOf { it.value })
}

solve(java.io.File(args[0]).readLines())
