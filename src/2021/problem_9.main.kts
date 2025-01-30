#!/usr/bin/env kotlin

val board = java.io.File(args[0]).readLines().map { it.map { it.digitToInt() } }

val xMax = board[0].lastIndex
val yMax = board.lastIndex

fun height(x: Int, y: Int): Int =
    if (x < 0 || x > xMax || y < 0 || y > yMax) 10 else board[y][x]

fun isLowPoint(x: Int, y: Int): Boolean {
    val h = height(x, y)
    return h < height(x - 1, y) && h < height(x + 1, y) &&
           h < height(x, y - 1) && h < height(x, y + 1)
}

data class Point(val x: Int, val y: Int)
val lowPoints = (0..xMax).flatMap { 
    x -> (0..yMax).mapNotNull { y -> if (isLowPoint(x, y)) Point(x, y) else null } 
}

println(lowPoints.sumOf { 1 + height(it.x, it.y)})

fun basinSize(start: Point): Int {
    val frontier = ArrayDeque(listOf(start))
    val seen = mutableSetOf<Point>()
    while (!frontier.isEmpty()) {
        val p = frontier.removeFirst()
        if (height(p.x, p.y) < 9 && seen.add(p)) {
            frontier.apply {
                add(Point(p.x - 1, p.y))
                add(Point(p.x + 1, p.y))
                add(Point(p.x, p.y - 1))
                add(Point(p.x, p.y + 1))
            }
        }
    }

    return seen.size
}

println(lowPoints.map(::basinSize).sorted().takeLast(3).reduce(Int::times))
