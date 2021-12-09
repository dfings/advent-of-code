#!/usr/bin/env kotlin

val board = java.io.File(args[0]).readLines().map { it.chunked(1).map { it.toInt() } }

val xMax = board[0].lastIndex
val yMax = board.lastIndex

fun height(x: Int, y: Int): Int =
    if (x < 0 || x > xMax || y < 0 || y > yMax) 10 else board[y][x]

fun isLowPoint(x: Int, y: Int): Boolean {
    val h = height(x, y)
    return h < height(x - 1, y) && h < height(x + 1, y) &&
           h < height(x, y - 1) && h < height(x, y + 1)
}

val lowPoints = (0..xMax).flatMap { 
    x -> (0..yMax).mapNotNull { y -> if (isLowPoint(x, y)) x to y else null } 
}

println(lowPoints.sumOf { (x, y) -> 1 + height(x, y)})
