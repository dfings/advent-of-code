#!/usr/bin/env kotlin

data class Board(val points: List<List<Int>>) {
    val xMax = points[0].lastIndex
    val yMax = points.lastIndex

    fun height(x: Int, y: Int): Int =
        if (x < 0 || x > xMax || y < 0 || y > yMax) 10 else points[y][x]

    fun isLowPoint(x: Int, y: Int): Boolean {
        val h = height(x, y)
        return h < height(x - 1, y) && h < height(x + 1, y) &&
               h < height(x, y - 1) && h < height(x, y + 1)
    }

    fun risk(x: Int, y: Int): Int = if (isLowPoint(x, y)) 1 + height(x, y) else 0

    fun totalRisk() = (0..xMax).flatMap { x -> (0..yMax).map { y -> risk(x, y) } }.sum()
}

val board = Board(java.io.File(args[0]).readLines().map { it.chunked(1).map { it.toInt() } })
println(board.totalRisk())
