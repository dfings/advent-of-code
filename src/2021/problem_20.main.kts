#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)

class Grid(val points: Map<Point, Char>, val default: Char = '.') {
    val xMin = points.keys.minOf { it.x }
    val xMax = points.keys.maxOf { it.x }
    val yMin = points.keys.minOf { it.y }
    val yMax = points.keys.maxOf { it.y }

    fun enhance(code: String): Grid {
        val newDefault = when {
            default == '.' && code.first() == '.' -> '.'
            default == '.' && code.first() == '#' -> '#'
            default == '#' && code.last() == '.' -> '.'
            default == '#' && code.last() == '#' -> '#'
            else -> error("Oops")
        }
        val newPoints = buildMap {
            for (x in (xMin - 2 .. xMax + 2)) {
                for (y in (yMin - 2 .. yMax + 2)) {
                    val p = Point(x, y)
                    put(p, code[toBinaryInt(p)])
                }
            }
        }
        return Grid(newPoints, newDefault)
    }

    fun valueAt(x: Int, y: Int): Int = valueAt(Point(x, y))
    fun valueAt(p: Point): Int = if ((points[p] ?: default) == '#') 1 else 0
    fun toBinaryInt(p: Point): Int = listOf(
        valueAt(p.x - 1, p.y - 1), valueAt(p.x, p.y - 1), valueAt(p.x + 1, p.y - 1),
        valueAt(p.x - 1, p.y),     valueAt(p),            valueAt(p.x + 1, p.y),
        valueAt(p.x - 1, p.y + 1), valueAt(p.x, p.y + 1), valueAt(p.x + 1, p.y + 1),
    ).joinToString("").toInt(radix = 2)

    fun countLights() = points.values.count { it == '#' }

    override fun toString(): String {
        return (yMin..yMax).map { y ->
            (xMin..xMax).map { x ->
                points.getValue(Point(x, y))
            }.joinToString("")
        }.joinToString("\n")
    }
}

val lines = java.io.File(args[0]).readLines()
val code = lines.first()
val grid = Grid(lines.drop(2).flatMapIndexed { y, line -> line.mapIndexed { x, char -> Point(x, y) to char } }.toMap())
println((1..2).fold(grid) { acc, _ -> acc.enhance(code) }.countLights())
println((1..50).fold(grid) { acc, _ -> acc.enhance(code) }.countLights())
