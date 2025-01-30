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

    data class State(val point: Point, val path: List<Direction>)
    fun State.neighbors() = cardinalDirections.map { State(point + it, path + it) }.filter { it.point in points }

    fun findShortestPaths(start: Char, end: Char): List<String> {
        if (start == end) listOf("A")
        val endPoint = pointMap.getValue(end)
        var minEndScore = Int.MAX_VALUE
        val frontier = ArrayDeque(listOf(State(pointMap.getValue(start), emptyList()) to 0))
        val foundPaths = mutableListOf<List<Direction>>()
        while (!frontier.isEmpty()) {
            val (state, score) = frontier.removeFirst()
            if (state.point == endPoint) {
                minEndScore = score
                foundPaths.add(state.path + Direction.PUSH)
            }
            for (newState in state.neighbors()) {
                val newScore = score + 1
                if (newScore <= minEndScore) {  
                    frontier.add(newState to newScore)
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
fun minLengthRecursive(line: String, remaining: Int): Long = cache.getOrPut(line to remaining) {
    ("A" + line).zipWithNext().sumOf { 
        dirPadMap.getValue(it).minOf {
            if (remaining == 1) it.length.toLong() else minLengthRecursive(it, remaining - 1)
        } 
    }
}

fun minLength(line: String, numDirPadRobots: Int): Long =
    line.zipWithNext().sumOf { 
        numberPathMap.getValue(it).minOf { minLengthRecursive(it, numDirPadRobots) } 
    }

val lines = java.io.File(args[0]).readLines()
println(lines.sumOf { it.dropLast(1).toLong() * minLength("A" + it, 2) })
println(lines.sumOf { it.dropLast(1).toLong() * minLength("A" + it, 25) })
