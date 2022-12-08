#!/usr/bin/env kotlin

class Grid(heights: List<List<Int>>) {
    val xMax = heights[0].lastIndex
    val yMax = heights.lastIndex
    val points = (0..xMax).map { x -> (0..yMax).map { y -> Point(x, y, heights[y][x]) } }

    inner class Point(val x: Int, val y: Int, val height: Int) {
        fun left() = (x - 1 downTo 0).map { points[it][y] }
        fun right() = (x + 1..xMax).map { points[it][y] }
        fun up() = (y - 1 downTo 0).map { points[x][it] }
        fun down() = (y + 1..yMax).map { points[x][it] }

        fun isVisible(): Boolean {
            fun List<Point>.visible() = all { it.height < height }
            return left().visible() || right().visible() || down().visible() || up().visible()
        }

        fun score(): Int {
            fun List<Point>.distance(): Int = minOf(takeWhile { it.height < height }.size + 1, size)
            return left().distance() * right().distance() * up().distance() * down().distance()
        }
    }
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(lines.map { it.map { "$it".toInt() } })
println(grid.points.flatten().count { it.isVisible() })
println(grid.points.flatten().maxOf { it.score() })
