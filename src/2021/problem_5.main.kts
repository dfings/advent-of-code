#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
data class Line(val a: Point, val b: Point) {
    val diagonal: Boolean = a.x != b.x && a.y != b.y
    val xStep: Int = b.x.compareTo(a.x)
    val yStep: Int = b.y.compareTo(a.y)
    val steps: Int = kotlin.math.abs(if (a.x == b.x) b.y - a.y else b.x - a.x)
    val points: List<Point> = (0..steps).map { Point(a.x + xStep * it, a.y + yStep * it) }
}

fun parsePoint(raw: List<String>) = Point(raw[0].toInt(), raw[1].toInt())
fun parseLine(raw: List<String>) = Line(parsePoint(raw.take(2)), parsePoint(raw.drop(2)))
val lines = java.io.File(args[0]).readLines().map { parseLine(it.split(",", " -> ")) }

val nonDiagonalPoints = lines.filterNot { it.diagonal }.flatMap { it.points }
println(nonDiagonalPoints.groupBy { it }.count { it.value.size > 1 })

val allPoints = lines.flatMap { it.points }
println(allPoints.groupBy { it }.count { it.value.size > 1 })
