#!/usr/bin/env kotlin

import kotlin.math.abs

val lines = java.io.File(args[0]).readLines()

data class Point(val x: Int, val y: Int)

val rowsToExpand = lines.indices.filter { y -> '#' !in lines[y] }
val colsToExpand = lines[0].indices.filter { x -> lines.none { it[x] == '#' } }

fun makePoint(x: Int, y: Int) = 
    Point(x + colsToExpand.count { x > it }, y + rowsToExpand.count { y > it })

val galaxies = lines.flatMapIndexed { y, line ->
  line.mapIndexedNotNull { x, char -> if (char == '#') makePoint(x, y) else null }
}

fun distance(a: Point, b: Point) = abs(b.x - a.x) + abs(b.y - a.y)

fun allPairs(points: Iterable<Point>): Iterable<Pair<Point, Point>> =
  points.flatMap { i -> points.mapNotNull { j -> i to j } }

println(allPairs(galaxies).sumOf { (a, b) -> distance(a, b) } / 2)

