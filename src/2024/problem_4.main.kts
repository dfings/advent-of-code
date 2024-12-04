#!/usr/bin/env kotlin

enum class Dir(val x: Int, val y: Int) {
    N(0, 1), NE(1, 1), E(1, 0), SE(1, -1), S(0, -1), SW(-1, -1), W(-1, 0), NW(-1, 1)
}

class Point(val x: Int, val y: Int) {
    fun move(d: Dir) = Point(x + d.x, y + d.y)
}

class Grid(val data: List<String>) {
    val xRange = 0..data[0].lastIndex
    val yRange = 0..data.lastIndex
    val points = xRange.flatMap { x -> yRange.map { y -> Point(x, y) } }
    fun get(p: Point) = if (p.x in xRange && p.y in yRange) data[p.y][p.x] else '.'
}

val lines = java.io.File(args[0]).readLines()
val grid = Grid(lines)

// Part 1
fun Grid.isXmas(p: Point, d: Dir) = 
    generateSequence(p) { it.move(d) }.map { get(it) }.take(4).joinToString("") == "XMAS"
println(grid.points.sumOf { p -> Dir.values().count { grid.isXmas(p, it) } })

// Part 2
val ms = setOf('M', 'S')
fun Grid.isMs(p: Point, d1: Dir, d2: Dir) = setOf(get(p.move(d1)), get(p.move(d2))) == ms
fun Grid.isXmas2(p: Point) = get(p) == 'A' && isMs(p, Dir.NW, Dir.SE) && isMs(p, Dir.NE, Dir.SW)
println(grid.points.count { grid.isXmas2(it) })
