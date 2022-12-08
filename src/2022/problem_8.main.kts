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
    }

    fun isVisible(p: Point): Boolean {
        fun isLower(p2: Point) = p2.height < p.height
        return p.left().all(::isLower) ||
            p.right().all(::isLower) ||
            p.down().all(::isLower) ||
            p.up().all(::isLower)
    }

    fun score(p: Point): Int {
        fun List<Point>.visible(): Int {
            var count = 0
            for (point in this) {
                count++
                if (point.height >= p.height) break
            }
            return count
        }
        return p.left().visible() * p.right().visible() * p.up().visible() * p.down().visible()
    }
}

val lines = java.io.File(args[0]).readLines()
val grid: Grid = Grid(lines.map { it.map { "$it".toInt() } })
println(grid.pointsList.count(grid::isVisible))
println(grid.pointsList.maxOf(grid::score))
