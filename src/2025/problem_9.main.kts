#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

data class Point(val x: Long, val y: Long)

data class Edge(val a: Point, val b: Point) {
    val xMin = min(a.x, b.x)
    val xMax = max(a.x, b.x)
    val yMin = min(a.y, b.y)
    val yMax = max(a.y, b.y)
    val isVertical = xMin == xMax
    val isHorizontal = yMin == yMax
}
fun Edge.isInXRange(x: Long) = xMin < x && x < xMax
fun Edge.isInYRange(y: Long) = yMin < y && y < yMax
fun Edge.intersects(e: Edge): Boolean =
    (isVertical && e.isHorizontal && e.isInXRange(a.x) && isInYRange(e.a.y)) ||
    (isHorizontal && e.isVertical && isInXRange(e.a.x) && e.isInYRange(a.y))

fun makeEdges(points: List<Point>) = (points + points[0]).zipWithNext().map { (a, b) -> Edge(a, b) }

data class Rectangle(val a: Point, val b: Point) {
    val d = Edge(a, b) // Diagonal
    val area = ((d.xMax - d.xMin + 2L) * (d.yMax - d.yMin + 2L)) / 4 // Unscale
    val points = listOf(
        Point(d.xMin + 1, d.yMin + 1),
        Point(d.xMax - 1, d.yMin + 1), 
        Point(d.xMax - 1, d.yMax - 1), 
        Point(d.xMin + 1, d.yMax - 1),
    )
    val edges = makeEdges(points)
}

class Polygon(val edges: List<Edge>) {
    fun contains(r: Rectangle): Boolean = r.points.all { contains(it) }  && r.edges.none { intersects(it) }

    fun contains(p: Point): Boolean {
        val ray = Edge(p, p.copy(x = 0))
        return edges.count { ray.intersects(it) } % 2 == 1
    }

    fun intersects(e: Edge): Boolean = edges.any { e.intersects(it) }
}

fun solve(lines: List<String>) {
    val points = lines.map { it.split(",").map { it.toLong() * 2 }.let { Point(it[0], it[1]) } }
    val polygon = Polygon(makeEdges(points))
    val rectangles = points.flatMapIndexed { i, a -> points.mapIndexedNotNull { j, b -> 
        if (i < j) Rectangle(a, b) else null 
    } }.sortedBy { -it.area }

    println(rectangles.first().area)
    println(rectangles.first { polygon.contains(it) }.area)
}

solve(java.io.File(args[0]).readLines())
