#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val grid = lines.map { it.map { "$it".toInt() } }

fun List<List<Int>>.isVisible(x: Int, y: Int): Boolean {
    val height = this[y][x]
    return (0..x - 1).all { this[y][it] < height } ||
        (x + 1..this[y].lastIndex).all { this[y][it] < height } ||
        (0..y - 1).all { this[it][x] < height } ||
        (y + 1..lastIndex).all { this[it][x] < height }
}

var count = 0
for (y in 0..grid.lastIndex) {
    for (x in 0..grid[y].lastIndex) {
        if (grid.isVisible(x, y)) count++
    }
}

println(count)

fun List<List<Int>>.score(x: Int, y: Int): Int {
    val height = this[y][x]
    var score1 = 0
    for (i in x - 1 downTo 0) {
        score1++
        if (this[y][i] >= height) break
    }
    var score2 = 0
    for (i in x + 1..this[y].lastIndex) {
        score2++
        if (this[y][i] >= height) break
    }
    var score3 = 0
    for (j in y - 1 downTo 0) {
        score3++
        if (this[j][x] >= height) break
    }
    var score4 = 0
    for (j in y + 1..lastIndex) {
        score4++
        if (this[j][x] >= height) break
    }
    return score1 * score2 * score3 * score4
}

data class Point(val x: Int, val y: Int, val score: Int)
val points = mutableListOf<Point>()
for (y in 0..grid.lastIndex) {
    for (x in 0..grid[y].lastIndex) {
        points.add(Point(x, y, grid.score(x, y)))
    }
}

println(points.maxBy { it.score }.score)
