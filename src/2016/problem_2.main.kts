#!/usr/bin/env kotlin

import kotlin.math.abs

enum class Direction(val x: Int, val y: Int) {
    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0) 
}

val directions = mapOf(
    'U' to Direction.UP, 
    'D' to Direction.DOWN, 
    'L' to Direction.LEFT, 
    'R' to Direction.RIGHT
)

data class Point(val x: Int, val y: Int)

val keypad1 = listOf("123", "456", "789")
val keypad2 = listOf("  1  ", " 234 ", "56789", " ABC ", "  D  ")

fun Point.move1(d: Direction) = Point((x + d.x).coerceIn(0..2), (y + d.y).coerceIn(0..2))
fun Point.move2(d: Direction): Point {
    val next = Point(x + d.x, y + d.y)
    return if (keypad2.getOrElse(next.y) { "" }.getOrElse(next.x) { ' ' } == ' ') this else next
}

fun getCode(
    start: Point, 
    input: List<List<Direction>>, 
    keypad: List<String>, 
    move: Point.(Direction) -> Point
): String {
    var p = start
    val code = mutableListOf<Char>()
    for (ds in input) {
        for (d in ds) {
            p = p.move(d)
        }
        code.add(keypad[p.y][p.x])
    }
    return code.joinToString("")
}

val lines = java.io.File(args[0]).readLines()
val input = lines.map { it.map { directions.getValue(it) } }

println(getCode(Point(1, 1), input, keypad1, Point::move1))
println(getCode(Point(0, 2), input, keypad2, Point::move2))
