#!/usr/bin/env kotlin

import java.util.PriorityQueue

data class Point(val x: Int, val y: Int)
data class Node<T>(val state: T, val cost: Int) : Comparable<Node<T>> {
    override fun compareTo(other: Node<T>) = cost.compareTo(other.cost)
}

class Graph(val costs: List<List<Int>>) {
    val xMax = costs[0].lastIndex
    val yMax = costs.lastIndex

    fun computeShortestPath(): Int {
        val frontier = PriorityQueue(listOf(Node(Point(0, 0), 0)))
        val seen = mutableSetOf<Point>()
        while (!frontier.isEmpty()) {
            val (point, cost) = frontier.poll()
            if (!seen.add(point)) continue
            if (point.x == xMax && point.y == yMax) return cost
            for (next in point.neighbors()) {
                frontier.add(Node(next, cost + costs[next.y][next.x]))
            }
        }
        return -1
    }

    fun Point.neighbors() = listOfNotNull(
        pointAt(x - 1, y), pointAt(x + 1, y), 
        pointAt(x, y - 1), pointAt(x, y + 1)
    )
    
    fun pointAt(x: Int, y: Int) = if (x in 0..xMax && y in 0..yMax) Point(x, y) else null
}

val lines = java.io.File(args[0]).readLines()
val originalMap = lines.map { it.map { it.digitToInt() } }

println(Graph(originalMap).computeShortestPath())

fun List<Int>.incrementBy(n: Int) = map { if (it + n < 10) it + n else 1 + ((it + n) % 10) }
val replicatedRight = originalMap.map { line ->
    (0..4).map { n -> line.incrementBy(n) }.flatten()
}
val fullMap = (0..4).flatMap { n ->
    replicatedRight.map { it.incrementBy(n) }
}
println(Graph(fullMap).computeShortestPath())
