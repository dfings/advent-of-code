#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
data class Line(val a: Point, val b: Point) {
    fun contains(p: Point) = when {
        a.x == b.x -> p.x == a.x && a.y <= p.y && p.y <= b.y
        a.y == b.y -> p.y == a.y && a.x <= p.x && p.x <= b.x
        else -> false
    }
}

fun parsePoint(raw: List<String>) = Point(raw[0].toInt(), raw[1].toInt())
fun parseLine(raw: List<String>): Line {
    val a = parsePoint(raw.take(2))
    val b = parsePoint(raw.drop(2))
    return if (a.x <= b.x  && a.y <= b.y) Line(a, b) else Line(b, a)
}
val lines = java.io.File(args[0]).readLines().map { parseLine(it.split(",", " -> ")) }

val maxX = lines.maxOf { it.b.x }
val maxY = lines.maxOf { it.b.y }
val points = (0..maxX).flatMap { x -> (0..maxY).map { y -> Point(x, y) } }
val overlap = points.filter { p -> lines.count { it.contains(p) } > 1 }
println(overlap.size)
