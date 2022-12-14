#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

enum class Material { ROCK, SAND }
data class Point(val x: Int, val y: Int)
data class Cave(val points: MutableMap<Point, Material>, val hasFloor: Boolean) {
    val source = Point(500, 0)
    val yMax = points.keys.maxOf { it.y } + if (hasFloor) 2 else 0

    fun dropAll() {
        while (dropNext()) {}
    }

    fun dropNext(): Boolean {
        var curr = source
        var prev: Point? = null
        while (prev != curr && curr.y < yMax - 1) {
            prev = curr
            curr = curr.next()
        }
        if (hasFloor || curr.next() == curr) {
            return points.put(curr, Material.SAND) == null
        }
        return false
    }

    fun Point.next() = when {
        Point(x, y + 1) !in points -> Point(x, y + 1)
        Point(x - 1, y + 1) !in points -> Point(x - 1, y + 1)
        Point(x + 1, y + 1) !in points -> Point(x + 1, y + 1)
        else -> this
    }
}

fun expand(a: Point, b: Point) = if (a.x == b.x) {
    (min(a.y, b.y)..max(a.y, b.y)).map { Point(a.x, it) }
} else {
    (min(a.x, b.x)..max(a.x, b.x)).map { Point(it, a.y) }
}

fun String.toPoint() = split(",").map { it.toInt() }.let { Point(it[0], it[1]) }
fun String.toLine() = split(" -> ").map { it.toPoint() }.windowed(2).map { expand(it[0], it[1]) }.flatten()

val lines = java.io.File(args[0]).readLines()
val points = lines.flatMap { it.toLine() }.map { it to Material.ROCK }.toMap()

val abyssCave = Cave(points.toMutableMap(), hasFloor = false)
abyssCave.dropAll()
println(abyssCave.points.values.count { it == Material.SAND })

val floorCave = Cave(points.toMutableMap(), hasFloor = true)
floorCave.dropAll()
println(floorCave.points.values.count { it == Material.SAND })

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
