#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Point(val x: Int, val y: Int)
object Gear
object Symbol
class Number(val value: Int)

val grid =
    buildMap {
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                when {
                    contains(Point(x, y)) -> null as Any?
                    char.isDigit() -> {
                        val numberString = line.substring(x).takeWhile { it.isDigit() }
                        val number = Number(numberString.toInt())
                        (0..numberString.lastIndex).forEach { i -> put(Point(x + i, y), number) }
                    }
                    char != '.' -> put(Point(x, y), if (char == '*') Gear else Symbol)
                }
            }
        }
    }

fun getAdjacent(p: Point) =
    (-1..1).flatMap { i -> (-1..1).mapNotNull { j -> grid.get(Point(p.x + i, p.y + j)) } }

fun isAdjacentToSymbol(p: Point): Boolean = getAdjacent(p).any { it is Gear || it is Symbol }

val schematicNumbers =
    grid.entries
        .mapNotNull { (k, v) -> if (v is Number && isAdjacentToSymbol(k)) v else null }
        .toSet()

println(schematicNumbers.map { it.value }.sum())

val schematicGearsNumbers =
    grid.entries
        .filter { it.value is Gear }
        .map { getAdjacent(it.key).filterIsInstance<Number>().toSet().map { it.value } }
        .filter { it.size == 2 }

println(schematicGearsNumbers.map { it.reduce(Int::times) }.sum())
