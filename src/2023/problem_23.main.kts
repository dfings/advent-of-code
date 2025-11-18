#!/usr/bin/env kotlin

import kotlin.math.max

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int, val invalid: Char) {
    UP(0, -1, 'v'), DOWN(0, 1, '^'), LEFT(-1, 0, '>'), RIGHT(1, 0, '<')
}
data class Segment(val from: Point, val to: Point, val weight: Int)

fun Point.move(dir: Direction) = Point(x + dir.dx, y + dir.dy)
fun Point.moveAll() = Direction.entries.map { move(it) }

class Graph(val lines: List<String>) {

    val xMax = lines[0].lastIndex
    val yMax = lines.lastIndex

    val segments = mutableSetOf<Segment>()
    val outgoing = mutableMapOf<Point, List<Segment>>()

    fun getValue(p: Point): Char = 
        if (p.x < 0 || p.y < 0 || p.x > xMax || p.y > yMax) '#' else lines[p.y][p.x]

    fun successors(p: Point): List<Point> = when (getValue(p)) {
        '>' -> listOf(Point(p.x + 1, p.y))
        '<' -> listOf(Point(p.x - 1, p.y))
        '^' -> listOf(Point(p.x, p.y - 1))
        'v' -> listOf(Point(p.x, p.y + 1))
        '#' -> emptyList()
        '.' -> Direction.entries.mapNotNull { dir ->
            val n = p.move(dir)
            if (getValue(n) == dir.invalid) null else n
        }
        else -> throw IllegalStateException("$p ${getValue(p)}")
    }.filter { getValue(it) != '#' }

    fun compressPaths(start: Point) {
        val points = (1..yMax).flatMap { y -> (1..xMax).map { x -> Point(x, y) } }
        val junctions = points.filter { p ->
            getValue(p) != '#' && p.moveAll().count { getValue(it) != '#' } > 2
        }.toSet() + start

        for (p in junctions) {
            val firstSteps = successors(p)
            for (firstStep in firstSteps) {
                var current = firstStep
                val seen = mutableSetOf(p)
                var next = successors(current).filter { it !in seen }
                while (next.size == 1) {
                    seen += current
                    current = next.single()
                    next = successors(current).filter { it !in seen }
                }
                segments.add(Segment(p, current, seen.size))
            }
        }
        outgoing += segments.groupBy { it.from }
    }
        
    fun longestPath(start: Point, end: Point, seen: Set<Point> = emptySet(), weight: Int = 0): Int {
        if (start == end) return weight
        val successors = outgoing[start]?.filter { it.to !in seen } ?: emptyList()
        return successors.maxOfOrNull { 
            longestPath(it.to, end, seen + start, weight + it.weight) 
        } ?: 0
    }
}

fun solve(lines: List<String>) {
    val start = Point(1, 0)
    val end = Point(lines[0].lastIndex - 1, lines.lastIndex)

    val graph = Graph(lines)
    graph.compressPaths(start)
    println(graph.longestPath(start, end))

    val noSlopes = lines.map { it.map { if (it == '#') it else '.' }.joinToString("") }
    val graph2 = Graph(noSlopes)
    graph2.compressPaths(start)
    println(graph2.longestPath(start, end))
}

solve(java.io.File(args[0]).readLines())
