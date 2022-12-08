#!/usr/bin/env kotlin

class Grid(heights: List<List<Int>>) {
    val xMax = heights[0].lastIndex
    val yMax = heights.lastIndex
    val points = heights.mapIndexed { y, it -> it.mapIndexed { x, it -> Point(x, y, it) } }
    val pointsList = (0..xMax).flatMap { x -> (0..yMax).map { y -> points[y][x] } }

    fun point(x: Int, y: Int) = points[y][x]

    inner class Point(val x: Int, val y: Int, val height: Int) {
        fun left() = (x - 1 downTo 0).map { point(it, y) }
        fun right() = (x + 1..xMax).map { point(it, y) }
        fun up() = (y - 1 downTo 0).map { point(x, it) }
        fun down() = (y + 1..yMax).map { point(x, it) }

        fun isVisible(): Boolean {
            fun List<Point>.visible() = all { it.height < height }
            return left().visible() || right().visible() || down().visible() || up().visible()
        }

        fun score(): Int {
            fun List<Point>.distance(): Int {
                var count = 0
                for (point in this) {
                    count++
                    if (point.height >= height) break
                }
                return count
            }
            return left().distance() * right().distance() * up().distance() * down().distance()
        }
    }
}

val lines = java.io.File(args[0]).readLines()
val grid: Grid = Grid(lines.map { it.map { "$it".toInt() } })
println(grid.pointsList.count { it.isVisible() })
println(grid.pointsList.maxOf { it.score() })
