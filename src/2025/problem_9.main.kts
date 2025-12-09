#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Long, val y: Long)
data class Rectangle(val a: Point, val b: Point) {
    val area = abs(a.x - b.x + 1L) * abs(a.y - b.y + 1L)
}

fun String.parse(): Point {
    val (x, y) = split(",").map { it.toLong() }
    return Point(x, y)
}

fun solve(lines: List<String>) {
    val points = lines.mapIndexed { i, it -> it.parse() }
    val rectangles = points.flatMapIndexed { i, a -> points.mapIndexedNotNull { j, b -> 
        if (i < j) Rectangle(a, b) else null 
    } }.sortedBy { -it.area }
    println(rectangles.first().area)
}

solve(java.io.File(args[0]).readLines())
