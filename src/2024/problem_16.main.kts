#!/usr/bin/env kotlin

import java.util.PriorityQueue
import kotlin.time.measureTime

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}
val rotateRight = (Direction.entries + listOf(Direction.NORTH)).zipWithNext().toMap()
val rotateLeft = (listOf(Direction.NORTH) + Direction.entries.reversed()).zipWithNext().toMap()

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

data class Reindeer(val p: Point, val d: Direction)
data class Node<T>(val state: T, val cost: Int) : Comparable<Node<T>> {
    override fun compareTo(other: Node<T>) = cost.compareTo(other.cost)
}

data class Maze(val start: Point, val walls: Set<Point>, val end: Point) {
    fun Reindeer.neighbors() = buildList {
        val left = rotateLeft.getValue(d)
        val right = rotateRight.getValue(d)
        if (p + d !in walls) add(copy(p = p + d) to 1)
        if (p + left !in walls) add(Reindeer(p = p + left, d = left) to 1001)
        if (p + right !in walls) add(Reindeer(p = p + right, d = right) to 1001)
    }

    fun findShortestPaths(): Pair<Int, Map<Reindeer, List<Reindeer>>> {
        val startReindeer = Reindeer(start, Direction.EAST)
        val minScores = mutableMapOf(startReindeer to 0)
        var minEndScore = Int.MAX_VALUE
        val previous = mutableMapOf<Reindeer, MutableList<Reindeer>>()
        val frontier = PriorityQueue<Node<Reindeer>>()
        froniter.add(Node(startReindeer, 0))
        while (!frontier.isEmpty()) {
            val (reindeer, score) = frontier.poll()
            if (reindeer.p == end) minEndScore = score
            for ((newReindeer, scoreDelta) in reindeer.neighbors()) {
                val newScore = score + scoreDelta
                val oldScore = minScores[newReindeer] ?: Int.MAX_VALUE
                if (newScore <= oldScore && newScore <= minEndScore) {  
                    minScores[newReindeer] = newScore
                    val prev = previous.getOrPut(newReindeer) { mutableListOf<Reindeer>() }
                    if (newScore < oldScore) {
                        frontier.add(newReindeer to newScore)
                        prev.clear()
                    }
                    prev += reindeer
                }
            }
        }
        return minEndScore to previous
    }
}

fun findPathPoints(start: Point, end: Point, previous: Map<Reindeer, List<Reindeer>>): Set<Point> {
    val frontier = ArrayDeque<Reindeer>(previous.keys.filter { it.p == end })
    val pathPoints = mutableSetOf<Reindeer>()
    while (!frontier.isEmpty()) {
        val current = frontier.removeFirst()
        if (pathPoints.add(current) && current.p != start) {
            frontier.addAll(previous.getValue(current))
        }
    }
    return pathPoints.map { it.p }.toSet()
}

fun parseMaze(lines: List<String>): Maze {
    val pointToChar = lines.flatMapIndexed { y, line -> 
        line.mapIndexed { x, it -> Point(x, y) to it }
    }
    return Maze(
        pointToChar.single { it.second == 'S'}.first,
        pointToChar.filter { it.second == '#' }.map { it.first }.toSet(),
        pointToChar.single { it.second == 'E'}.first,
    )
}

val lines = java.io.File(args[0]).readLines()
val maze = parseMaze(lines)
val (minScore, previous) = maze.findShortestPaths()
println(minScore)
val pathPoints = findPathPoints(maze.start, maze.end, previous)
println(pathPoints.size)

// Temporary for determining timing.
var len = 0
repeat (0) {
    val t = measureTime {
        val (minScore, previous) = maze.findShortestPaths()
        len += findPathPoints(maze.start, maze.end, previous).size
    }
    println("$t $len")
}

// Temporary for visualizing solutions.
if (false) {
    for (y in lines.indices) {
    val builder = mutableListOf<String>()
        for (x in 0..lines[y].lastIndex) {
            val p = Point(x, y)
            builder.add(when {
                p in pathPoints -> "O"
                p in maze.walls -> "#"
                else -> "."
            })
        }
        println(builder.joinToString(""))
    }
}
