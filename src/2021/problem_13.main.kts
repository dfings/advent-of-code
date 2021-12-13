#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)

typealias Fold = (Point) -> Point
fun FoldX(x: Int): Fold = { if (it.x < x) it else Point(2 * x - it.x, it.y)}
fun FoldY(y: Int): Fold = { if (it.y < y) it else Point(it.x, 2 * y - it.y)}

fun String.toPoint() = split(',').let { Point(it[0].toInt(), it[1].toInt()) }

val lines = java.io.File(args[0]).readLines()
val points = lines.filter { ',' in it }.map{ it.toPoint() }
val folds = lines.filter { it.startsWith("fold along ") }.map {
    val (command, value) = it.split("=")
    when (command) {
        "fold along x" -> FoldX(value.toInt())
        "fold along y" -> FoldY(value.toInt())
        else -> throw IllegalStateException()
    }
}

// Part 1
println(points.map(folds[0]).distinct().size)

// Part 2
val finalPoints = folds.fold(points) { acc, it -> acc.map(it) }.toSet()
val xMax = finalPoints.maxOf { it.x }
val yMax = finalPoints.maxOf { it.y }
for (y in (0..yMax)) {
    println((0..xMax).map { if (Point(it, y) in finalPoints) '#' else ' '}.joinToString(""))
}
