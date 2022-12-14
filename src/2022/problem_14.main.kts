#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

enum class Material { ROCK, SAND }
data class Point(val x: Int, val y: Int)
data class Cave(val points: MutableMap<Point, Material>) {
    val source = Point(500, 0)
    val minPoint: Point 
        get() = Point(points.minOf { (k, v) -> k.x }, points.minOf { (k, v) -> k.y } )
    val maxPoint = Point(points.maxOf { (k, v) -> k.x }, points.maxOf { (k, v) -> k.y } )
    
    fun dropAll() {
        while(dropNext()) {}
    }

    fun dropNext(): Boolean {
        var curr = source
        var prev: Point? = null
        while (prev != curr && curr.y <= maxPoint.y) {
            prev = curr
            curr = curr.next()
        }
        if (curr.y <= maxPoint.y) points[curr] = Material.SAND
        return curr.y <= maxPoint.y
    }

    fun Point.next() = when {
        Point(x, y + 1) !in points -> Point(x, y + 1)
        Point(x - 1, y + 1) !in points -> Point(x - 1, y + 1)
        Point(x + 1, y + 1) !in points -> Point(x + 1, y + 1)
        else -> this
    }

    fun print() {
        for (y in minPoint.y..maxPoint.y) {
            println((minPoint.x..maxPoint.x).map { x -> when (points[Point(x, y)]) {
                Material.ROCK -> "X"
                Material.SAND -> "o"
                else -> "."
        }}.joinToString(""))
        }
    }
}

fun expand(a: Point, b: Point) = 
    if (a.x == b.x) {
        (min(a.y, b.y)..max(a.y, b.y)).map { Point(a.x, it) }
    } else {
        (min(a.x, b.x)..max(a.x, b.x)).map { Point(it, a.y) }
    }

fun String.toPoint() = split(",").map { it.toInt() }.let { Point(it[0], it[1]) }
fun String.toLine() = split(" -> ").map { it.toPoint() }.windowed(2).map { expand(it[0], it[1]) }.flatten()

val lines = java.io.File(args[0]).readLines()
val points = lines.flatMap { it.toLine() }
println(points.size)
val cave = Cave(points.map { it to Material.ROCK }.toMap().toMutableMap())
cave.print()
cave.dropAll()
println(cave.points.values.count { it == Material.SAND })
cave.print()
