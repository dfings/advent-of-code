#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)

sealed interface Fold {
    fun apply(p: Point): Point
    data class X(val x: Int): Fold {
        override fun apply(p: Point) = if (p.x < x) p else Point(2 * x - p.x, p.y)
    }
    data class Y(val y: Int): Fold {
        override fun apply(p: Point) = if (p.y < y) p else Point(p.x, 2 * y - p.y)
    }
}

fun String.toPoint() = split(',').let { Point(it[0].toInt(), it[1].toInt()) }

val lines = java.io.File(args[0]).readLines()
val points = lines.filter { ',' in it }.map{ it.toPoint() }
val folds = lines.filter { it.startsWith("fold along ") }.map {
    val (command, value) = it.split("=")
    when (command) {
        "fold along x" -> Fold.X(value.toInt())
        "fold along y" -> Fold.Y(value.toInt())
        else -> throw IllegalStateException()
    }
}

// Part 1
println(points.map(folds[0]::apply).distinct().size)

// Part 2
val finalPoints = folds.fold(points) { p, f -> p.map(f::apply) }.toSet()
val xMax = finalPoints.maxOf { it.x }
val yMax = finalPoints.maxOf { it.y }
for (y in (0..yMax)) {
    println((0..xMax).map { if (Point(it, y) in finalPoints) '#' else ' '}.joinToString(""))
}
