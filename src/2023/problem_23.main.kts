#!/usr/bin/env kotlin

import kotlin.math.max

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int, val invalid: Char) {
    UP(0, -1, 'v'), DOWN(0, 1, '^'), LEFT(-1, 0, '>'), RIGHT(1, 0, '<')
}
data class Graph(val lines: List<String>)

fun Point.move(dir: Direction) = Point(x + dir.dx, y + dir.dy)

fun Graph.getValue(p: Point) = if (p.x < 0 || p.y < 0 || p.x > lines[0].lastIndex || p.y > lines.lastIndex) '#' else lines[p.y][p.x]
fun Graph.successors(p: Point): List<Point> = when (lines[p.y][p.x]) {
    '>' -> listOf(Point(p.x + 1, p.y))
    '<' -> listOf(Point(p.x - 1, p.y))
    '^' -> listOf(Point(p.x, p.y - 1))
    'v' -> listOf(Point(p.x, p.y + 1))
    '#' -> emptyList()
    '.' -> Direction.entries.mapNotNull { dir ->
        val n = p.move(dir)
        if (getValue(n) == dir.invalid) null else n
    }
    else -> throw IllegalStateException("$p")
}.filter { getValue(it) != '#' }

fun Graph.walk(start: Point, visited: Set<Point>): Set<Point> {
    val seen = visited.toMutableSet()
    var current = start
    seen += current
    var next = successors(current).filter { it !in seen }
    while (next.size == 1) {
        current = next.single()
        seen += current
        next = successors(current).filter { it !in seen }
    }
    return when {
        current.y == lines.lastIndex -> seen
        next.isEmpty() -> emptySet()
        else -> {
            val candidates = next.map { walk(it, seen) }
            val best = candidates.maxBy { it.size }
            if (best.isEmpty()) emptySet() else seen + best
        }
    }
}

fun solve(lines: List<String>) {
    val graph = Graph(lines)
    println(graph.walk(Point(1, 0), emptySet()).size - 1)
}

solve(java.io.File(args[0]).readLines())
