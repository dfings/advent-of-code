#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
data class Line(val a: Point, val b: Point) {
    val diagonal: Boolean = a.x != b.x && a.y != b.y
    fun contains(p: Point): Boolean = when {
        a.x == b.x -> p.x == a.x && a.y <= p.y && p.y <= b.y
        a.y == b.y -> p.y == a.y && a.x <= p.x && p.x <= b.x
        a.y < b.y -> a.x <= p.x && a.y <= p.y && p.x <= b.x && p.y <= b.y && p.x - a.x == p.y - a.y
        else -> a.x <= p.x && b.y <= p.y && p.x <= b.x && p.y <= a.y && p.x - a.x == a.y - p.y
    }
}

fun parsePoint(raw: List<String>) = Point(raw[0].toInt(), raw[1].toInt())
fun parseLine(raw: List<String>): Line {
    val a = parsePoint(raw.take(2))
    val b = parsePoint(raw.drop(2))
    return if (a.x < b.x  || (a.x == b.x && a.y < b.y)) Line(a, b) else Line(b, a)
}
val lines = java.io.File(args[0]).readLines().map { parseLine(it.split(",", " -> ")) }
val nonDiagonal = lines.filterNot { it.diagonal }

val maxX = lines.maxOf { it.b.x }
val maxY = lines.maxOf { it.b.y }
val points = (0..maxX).flatMap { x -> (0..maxY).map { y -> Point(x, y) } }
val overlap1 = points.filter { p -> nonDiagonal.count { it.contains(p) } > 1 }
val overlap2 = points.filter { p -> lines.count { it.contains(p) } > 1 }

println(overlap1.size)
println(overlap2.size)
