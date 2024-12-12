#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), NORTH_EAST(1, -1), EAST(1, 0), SOUTH_EAST(1, 1), 
    SOUTH(0, 1), SOUTH_WEST(-1,  1), WEST(-1, 0), NORTH_WEST(-1, -1)
}

class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

class Grid(val data: List<String>) {
    val xRange = 0..data[0].lastIndex
    val yRange = 0..data.lastIndex
    val points = xRange.flatMap { x -> yRange.map { y -> Point(x, y) } }
    fun at(p: Point) = if (p.x in xRange && p.y in yRange) data[p.y][p.x] else '.'
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(lines)

// Part 1
fun Grid.isXmas(p: Point, d: Direction) = 
    generateSequence(p) { it + d }.map { at(it) }.take(4).joinToString("") == "XMAS"
println(grid.points.sumOf { p -> Direction.entries.count { grid.isXmas(p, it) } })

// Part 2
val ms = setOf('M', 'S')
fun Grid.isMs(p: Point, d1: Direction, d2: Direction) = setOf(at(p + d1), at(p + d2)) == ms
fun Grid.isXmas2(p: Point) = at(p) == 'A' &&
    isMs(p, Direction.NORTH_WEST, Direction.SOUTH_EAST) &&
    isMs(p, Direction.NORTH_EAST, Direction.SOUTH_WEST)
println(grid.points.count { grid.isXmas2(it) })
