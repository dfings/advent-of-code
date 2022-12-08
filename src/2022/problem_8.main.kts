#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Point(val x: Int, val y: Int)
data class Grid(val heights: List<List<Int>>) {
    val xMax = heights[0].lastIndex
    val yMax = heights.lastIndex
    val points = (0..xMax).flatMap { x -> (0..yMax).map { y -> Point(x, y) } }
    fun height(x: Int, y: Int) = heights[y][x]
}

fun Grid.isVisible(x: Int, y: Int): Boolean {
    fun isLower(x2: Int, y2: Int) = height(x2, y2) < height(x, y)
    return (x - 1 downTo 0).all { isLower(it, y) } ||
        (x + 1..xMax).all { isLower(it, y) } ||
        (y - 1 downTo 0).all { isLower(x, it) } ||
        (y + 1..yMax).all { isLower(x, it) }
}

fun Grid.score(x: Int, y: Int): Int {
    fun countVisible(points: List<Point>): Int {
        var count = 0
        for (p in points) {
            count++
            if (height(p.x, p.y) >= height(x, y)) break
        }
        return count
    }
    val score1 = countVisible((x - 1 downTo 0).map { Point(it, y) })
    val score2 = countVisible((x + 1..xMax).map { Point(it, y) })
    val score3 = countVisible((y - 1 downTo 0).map { Point(x, it) })
    val score4 = countVisible((y + 1..yMax).map { Point(x, it) })
    return score1 * score2 * score3 * score4
}

val grid: Grid = Grid(lines.map { it.map { "$it".toInt() } })

println(grid.points.count { grid.isVisible(it.x, it.y) })
println(grid.points.maxOf { grid.score(it.x, it.y) })
