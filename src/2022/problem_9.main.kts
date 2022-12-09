#!/usr/bin/env kotlin

import kotlin.math.abs
import kotlin.math.sign

data class Motion(val direction: String, val count: Int)
data class Point(val x: Int, val y: Int)

fun Point.move(dx: Int, dy: Int) = if (dx == 0 && dy == 0) this else Point(x + dx, y + dy)
fun Point.move(direction: String) = when (direction) {
    "R" -> move(1, 0)
    "L" -> move(-1, 0)
    "U" -> move(0, 1)
    else -> move(0, -1)
}

fun Point.nextTo(other: Point) = abs(other.x - x) <= 1 && abs(other.y - y) <= 1
fun Point.follow(other: Point) = move(
    dx = if (other.x == x || nextTo(other)) 0 else (other.x - x).sign,
    dy = if (other.y == y || nextTo(other)) 0 else (other.y - y).sign
)

typealias Rope = MutableList<Point>
fun Rope(tailSize: Int) = (0..tailSize).map { Point(0, 0) }.toMutableList()
fun Rope.move(motions: List<Motion>): Int {
    val seen = mutableSetOf<Point>(last())
    for (motion in motions) {
        repeat(motion.count) {
            forEachIndexed { i, point ->
                set(i, if (i == 0) point.move(motion.direction) else point.follow(get(i - 1)))
            }
            seen.add(last())
        }
    }
    return seen.size
}

val lines = java.io.File(args[0]).readLines()
val motions = lines.map { it.split(" ") }.map { Motion(it[0], it[1].toInt()) }

println(Rope(1).move(motions))
println(Rope(9).move(motions))
