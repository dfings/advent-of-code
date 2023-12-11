#!/usr/bin/env kotlin

import kotlin.math.abs

val lines = java.io.File(args[0]).readLines()

data class Point(val x: Long, val y: Long)

val rowsToExpand = lines.indices.filter { y -> '#' !in lines[y] }
val colsToExpand = lines[0].indices.filter { x -> lines.none { it[x] == '#' } }

fun makePoint(x: Int, y: Int, e: Long) = Point(
    x.toLong() + colsToExpand.count { x > it } * (e - 1), 
    y.toLong() + rowsToExpand.count { y > it } * (e - 1)
)

fun galaxies(e: Long) = lines.flatMapIndexed { y, line ->
  line.mapIndexedNotNull { x, char -> if (char == '#') makePoint(x, y, e) else null }
}

fun allPairs(points: List<Point>): List<Pair<Point, Point>> =
  points.flatMapIndexed { i, a -> points.subList(i + 1, points.size).map { b -> a to b } }

fun distance(a: Point, b: Point) = abs(b.x - a.x) + abs(b.y - a.y)
fun distances(points: List<Point>) = allPairs(points).sumOf { (a, b) -> distance(a, b) }

println(distances(galaxies(2)))
println(distances(galaxies(1000000)))

