#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int, val z: Int)

class Pond(val points: Set<Point>) {

    val totalSurfaceArea: Int get() = points.sumOf { it.surfaceArea() }

    fun Point.surfaceArea() =
        listOf(
            copy(x = x - 1),
            copy(x = x + 1),
            copy(y = y - 1),
            copy(y = y + 1),
            copy(z = z - 1),
            copy(z = z + 1)
        ).count { it !in points }
}

val lines = java.io.File(args[0]).readLines()
val points = lines.map { it.split(",").map { it.toInt() } }.map { (x, y, z) -> Point(x, y, z) }
val pond = Pond(points.toSet())
println(pond.totalSurfaceArea)
