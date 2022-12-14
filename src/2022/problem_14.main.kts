#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

enum class Material { ROCK, SAND }
data class Point(val x: Int, val y: Int)
class Cave(val points: MutableMap<Point, Material>) {
    val source = Point(500, 0)
    val yMax = points.keys.maxOf { it.y }
    var path = mutableListOf(source)

    fun dropAll(): Int {
        while (dropNext()) {}
        return points.values.count { it == Material.SAND }
    }

    fun dropNext(): Boolean {
        var curr = path.removeLast()
        var prev: Point? = null
        while (prev != curr && curr.y < yMax - 1) {
            prev = curr
            curr = curr.next()
            if (prev != curr) {
                path.add(prev)
            }
        }
        if (curr.next() == curr) {
            points[curr] = Material.SAND
            return !path.isEmpty()
        }
        return false
    }

    val dxs = listOf(0, -1, 1)
    fun Point.next() : Point = dxs.firstNotNullOfOrNull { dx ->
        Point(x + dx, y + 1).let { if (it !in points) it else null }
    } ?: this
}

fun Point.to(other: Point) = if (x == other.x) {
    (min(y, other.y)..max(y, other.y)).map { Point(x, it) }
} else {
    (min(x, other.x)..max(x, other.x)).map { Point(it, y) }
}

fun String.toPoint() = split(",").map { it.toInt() }.let { Point(it[0], it[1]) }
fun String.toPoints() = split(" -> ").map { it.toPoint() }.windowed(2).map { it[0].to(it[1]) }.flatten()

val lines = java.io.File(args[0]).readLines()
val points = lines.flatMap { it.toPoints() }
fun List<Point>.toMutableMap() = map { it to Material.ROCK }.toMap().toMutableMap()

val abyssCave = Cave(points.toMutableMap())
println(abyssCave.dropAll())

val yMax = points.maxOf { it.y }
val floor = Point(500 - yMax - 3, yMax + 2).to(Point(500 + yMax + 3, yMax + 2))
val floorCave = Cave((points + floor).toMutableMap())
println(floorCave.dropAll())

// Debug printing.
fun Cave.print() {
    for (y in points.keys.minOf { it.y }..points.keys.maxOf { it.y }) {
        val line = (points.keys.minOf { it.x }..points.keys.maxOf { it.x }).map { x ->
            when (points[Point(x, y)]) {
                Material.ROCK -> "X"
                Material.SAND -> "o"
                else -> "."
            }
        }.joinToString("")
        println(line)
    }
}

if (args.size > 1 && args[1] == "-p") {
    abyssCave.print()
    println()
    floorCave.print()
}
