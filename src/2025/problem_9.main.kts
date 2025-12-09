#!/usr/bin/env kotlin

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Point(val x: Long, val y: Long)

data class Edge(val a: Point, val b: Point) {
    val xMin = min(a.x, b.x)
    val xMax = max(a.x, b.x)
    val yMin = min(a.y, b.y)
    val yMax = max(a.y, b.y)

    fun contains(p: Point) = 
        (xMin == xMax && p.x == xMin && yMin <= p.y && p.y <= yMax) ||
        (yMin == yMax && p.y == yMin && xMin <= p.x && p.x <= xMax)
}

data class Rectangle(val a: Point, val b: Point) {
    val area = abs(a.x - b.x + 1L) * abs(a.y - b.y + 1L)
    val points = listOf(a, Point(a.x, b.y), b, Point(b.x, a.y))
    val edges = (points + a).zipWithNext().map { (i, j) -> Edge(i, j) }
}

class Polygon(val edges: List<Edge>) {
    fun contains(p: Point): Boolean {
        val edge = Edge(p, p.copy(x = 0))
        return edges.any { it.contains(p) } || edges.count { intersect(it, edge) } % 2 == 1
    }

    fun contains(r: Rectangle): Boolean {
        val test1 = r.points.all { contains(it) } 
        val test2 = r.edges.none { intersects(it) }
        println("POINTS INSIDE: $test1, NO INTERSECTS: $test2")
        return test1 && test2
    }

    fun intersects(e: Edge): Boolean = edges.any { intersect(it, e) }
}

fun intersect2(e1: Edge, e2: Edge) = when {
    e1.xMin == e1.xMax && e2.xMin == e2.xMax -> false
    e1.yMin == e1.yMax && e2.yMin == e2.yMax -> false
    e1.xMin == e1.xMax -> e2.xMin < e1.xMin && e1.xMin < e2.xMax && e1.yMin < e2.yMin && e2.yMin < e1.yMax
    else -> e1.xMin < e2.xMin && e2.xMin < e1.xMax && e2.yMin < e1.yMin && e1.yMin < e2.yMax
}


fun intersect(e1: Edge, e2: Edge): Boolean {
    val out = intersect2(e1, e2)
    println("$e1 $e2 $out")
    return out
}

fun String.parse(): Point {
    val (x, y) = split(",").map { it.toLong() }
    return Point(x, y)
}

fun solve(lines: List<String>) {
    val points = lines.mapIndexed { i, it -> it.parse() }
    val polygon = Polygon((points + points[0]).zipWithNext().map { (a, b) -> Edge(a, b) })
    val rectangles = points.flatMapIndexed { i, a -> points.mapIndexedNotNull { j, b -> 
        if (i < j) Rectangle(a, b) else null 
    } }.sortedBy { -it.area }

    println(rectangles.first().area)
    println(rectangles.filter { 
        println("TESTING $it")
        polygon.contains(it) 
    }.map { it.area })
}

solve(java.io.File(args[0]).readLines())
