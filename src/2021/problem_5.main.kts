#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
data class Line(val a: Point, val b: Point) {
    val diagonal: Boolean = a.x != b.x && a.y != b.y
    val points: List<Point> get() = when {
        a.x == b.x -> (a.y..b.y).map { Point(a.x, it) }
        a.y == b.y -> (a.x..b.x).map { Point(it, a.y) }
        a.y < b.y -> (0..b.x-a.x).map { Point(a.x + it, a.y + it) }
        else -> (0..b.x-a.x).map { Point(a.x + it, a.y - it)}
    }
}

fun parsePoint(raw: List<String>) = Point(raw[0].toInt(), raw[1].toInt())
fun parseLine(raw: List<String>): Line {
    val a = parsePoint(raw.take(2))
    val b = parsePoint(raw.drop(2))
    return if (a.x < b.x  || (a.x == b.x && a.y < b.y)) Line(a, b) else Line(b, a)
}
val lines = java.io.File(args[0]).readLines().map { parseLine(it.split(",", " -> ")) }

val nonDiagonalPoints = lines.filterNot { it.diagonal }.flatMap { it.points }
println(nonDiagonalPoints.groupBy { it }.count { it.value.size > 1 })

val allPoints = lines.flatMap { it.points }
println(allPoints.groupBy { it }.count { it.value.size > 1 })
