#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Point(val x: Int, val y: Int)

sealed interface Content
sealed interface Symbol : Content
object Gear : Symbol
object OtherSymbol : Symbol
class Number(val value: Int) : Content

val grid =
    buildMap {
        for (y in 0..lines.lastIndex) {
            var x = 0
            while (x < lines[y].lastIndex) {
                val char = lines[y][x]
                if (char.isDigit()) {
                    val numberString = lines[y].substring(x).takeWhile { it.isDigit() }
                    val number = Number(numberString.toInt())
                    (0..numberString.lastIndex).forEach { i -> put(Point(x + i, y), number) }
                    x += numberString.length
                } else {
                    if (char != '.') {
                        put(Point(x, y), if (char == '*') Gear else OtherSymbol)
                    }
                    x += 1
                }
            }
        }
    }

fun getAdjacentContent(p: Point): List<Content> =
   (-1..1).flatMap { i -> (-1..1).mapNotNull { j -> grid.get(Point(p.x + i, p.y + j))} }

fun isAdjacentToSymbol(p: Point): Boolean = getAdjacentContent(p).any { it is Symbol}

val schematicNumbers =
    grid.entries
        .filter { it.value is Number && isAdjacentToSymbol(it.key) }
        .map { it.value as Number }
        .toSet()

println(schematicNumbers.map { it.value }.sum())

val schematicGearsNumbers = 
    grid.entries
        .filter { it.value is Gear }
        .map { getAdjacentContent(it.key).filterIsInstance<Number>().toSet() }
        .filter { it.size == 2 }
        .map { it.toList() }

println(schematicGearsNumbers.map { it[0].value * it[1].value }.sum())
