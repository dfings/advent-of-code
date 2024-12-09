#!/usr/bin/env kotlin

fun <T> Iterable<T>.allPairs() =
    flatMap { i -> mapNotNull { j -> if(i == j) null else i to j }}

data class Point(val x: Int, val y: Int)
class Antenna(val grid: List<String>) {
    val xRange = 0..grid[0].lastIndex
    val yRange = 0..grid.lastIndex
    val frequencyPoints = xRange.flatMap { x -> yRange.map { y -> grid[y][x] to Point(x, y) } }
        .filter { it.first != '.' }
        .groupBy( { it.first }, { it.second })

    operator fun contains(p: Point) = p.x in xRange && p.y in yRange

    fun generateAntinodes(a: Point, b: Point): Sequence<Point> {
        val dx = b.x - a.x
        val dy = b.y - a.y
        return generateSequence(b) { Point(it.x + dx, it.y + dy) }.takeWhile { it in this }
    }

    fun pairwiseAntinodes(points: Iterable<Point>, select: (Sequence<Point>) -> Sequence<Point>) = 
        points.allPairs().flatMap { (a, b) -> select(generateAntinodes(a, b)) }

    fun countAntinodes(select: (Sequence<Point>) -> Sequence<Point>) = 
        frequencyPoints.values.flatMap { pairwiseAntinodes(it, select) }.toSet().size
}

val lines = java.io.File(args[0]).readLines()
val antenna = Antenna(lines)
println(antenna.countAntinodes { it.drop(1).take(1) })
println(antenna.countAntinodes { it })
