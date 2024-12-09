#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
data class Vector(val dx: Int, val dy: Int)
fun Vector(a: Point, b: Point) = Vector(b.x  - a.x, b.y - a.y)

fun <T> Iterable<T>.allPairs() =
    flatMap { i -> mapNotNull { j -> if(i == j) null else i to j }}

class Antenna(val grid: List<String>) {
    val xRange = 0..grid[0].lastIndex
    val yRange = 0..grid.lastIndex
    val pointMap = xRange.flatMap { x -> yRange.map { y -> grid[y][x] to Point(x, y) } }
        .filter { it.first != '.' }
        .groupBy( { it.first }, { it.second })

    operator fun contains(p: Point) = p.x in xRange && p.y in yRange

    fun generateAntinodes(a: Point, b: Point): Sequence<Point> {
        val v = Vector(a, b)
        return generateSequence(b) { Point(it.x + v.dx, it.y + v.dy) }.takeWhile { it in this }
    }

    fun pairwiseAntinodes(points: Iterable<Point>, select: (Sequence<Point>) -> Sequence<Point>) = 
        points.allPairs().flatMap { (a, b) -> select(generateAntinodes(a, b)) }

    fun countAntinodes(select: (Sequence<Point>) -> Sequence<Point>) = 
        pointMap.values.flatMap { pairwiseAntinodes(it, select) }.toSet().size
}

val lines = java.io.File(args[0]).readLines()
val antenna = Antenna(lines)
println(antenna.countAntinodes { it.drop(1).take(1) })
println(antenna.countAntinodes { it })
