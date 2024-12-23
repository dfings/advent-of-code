#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int, val c: Char) {
    UP(0, -1, '^'), RIGHT(1, 0, '>'), DOWN(0, 1, 'v'), LEFT(-1, 0, '<'), PUSH(0, 0, 'A')
}
val cardinalDirections = Direction.entries.filter { it != Direction.PUSH }

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

class Pad(lines: List<String>) {
    val pointMap = lines.flatMapIndexed { 
        y, line -> line.mapIndexed { x, it -> it to Point(x, y) } 
    }.filter { it.first != ' ' }.toMap()
    val points = pointMap.values.toSet()

    data class Node(val point: Point, val path: List<Direction>)
    fun Node.neighbors() = cardinalDirections.map { Node(point + it, path + it) }.filter { it.point in points }

    fun findShortestPaths(start: Char, end: Char): List<String> {
        if (start == end) listOf("A")
        val endPoint = pointMap.getValue(end)
        var minEndScore = Int.MAX_VALUE
        val frontier = mutableSetOf(Node(pointMap.getValue(start), emptyList()) to 0)
        val foundPaths = mutableListOf<List<Direction>>()
        while (!frontier.isEmpty()) {
            val (node, score) = frontier.minBy { it.second }
            frontier.remove(node to score)
            if (node.point == endPoint) {
                minEndScore = score
                foundPaths.add(node.path + Direction.PUSH)
            }
            for (newNode in node.neighbors()) {
                val newScore = score + 1
                if (newScore <= minEndScore) {  
                    frontier.add(newNode to newScore)
                }
            }
        }
        return foundPaths.map { it.map { it.c }.joinToString("") }
    }

    fun makeShortestPathMap(): Map<Pair<Char, Char>, List<String>> = 
        pointMap.keys.flatMap { start ->
            pointMap.keys.mapNotNull { end ->
                (start to end) to findShortestPaths(start, end)
            }
        }.toMap()
}


val numberPad = Pad(listOf("789", "456", "123", " 0A"))
val numberPathMap = numberPad.makeShortestPathMap()

val dirPad = Pad(listOf(" ^A", "<v>"))
val dirPadMap = dirPad.makeShortestPathMap()

val cache = mutableMapOf<Pair<String, Int>, Long>()
fun findMinDirPadLength(line: String, remainingDepth: Int): Long = cache.getOrPut(line to remainingDepth) {
    val stateTransitions: List<List<String>> = ("A" + line).zipWithNext().map { dirPadMap.getValue(it) }
    if (remainingDepth == 0) {
        stateTransitions.map  { it.minBy { it.length } }.sumOf { it.length }.toLong()
    } else {
        stateTransitions.map { it.map { findMinDirPadLength(it, remainingDepth - 1) }.min() }.sum()
    }
}

fun findMinSequence(line: String, numDirPadRobots: Int): Long {
    val stateTransitions = line.zipWithNext().map { numberPathMap.getValue(it) }
    return stateTransitions.map { it.map { findMinDirPadLength(it, numDirPadRobots - 1) }.min() }.sum()
}

val lines = java.io.File(args[0]).readLines()
println(lines.sumOf { it.dropLast(1).toLong() * findMinSequence("A" + it, 2) })
println(lines.sumOf { it.dropLast(1).toLong() * findMinSequence("A" + it, 25) })
