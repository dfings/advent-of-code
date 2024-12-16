#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}
val rotateRight = (Direction.entries + listOf(Direction.NORTH)).zipWithNext().toMap()
val rotateLeft = (listOf(Direction.NORTH) + Direction.entries.reversed()).zipWithNext().toMap()

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

data class Reindeer(val p: Point, val d: Direction)

data class Maze(val start: Point, val walls: Set<Point>, val end: Point) {
    fun Reindeer.neighbors() = buildList {
        val left = rotateLeft.getValue(d)
        val right = rotateRight.getValue(d)
        if (p + d !in walls) add(copy(p = p + d) to 1)
        if (p + left !in walls) add(copy(d = left) to 1000)
        if (p + right !in walls) add(copy(d = right) to 1000)
    }

    fun findShortestPaths(): Pair<Int, Map<Reindeer, Set<Reindeer>>> {
        val startReindeer = Reindeer(start, Direction.EAST)
        val minScores = mutableMapOf(startReindeer to 0)
        var minEndScore = Int.MAX_VALUE
        val previous = mutableMapOf<Reindeer, MutableSet<Reindeer>>()
        val frontier = mutableSetOf(startReindeer)
        while (!frontier.isEmpty()) {
            val reindeer = frontier.minBy { minScores.getValue(it) }
            val score = minScores.getValue(reindeer)
            if (reindeer.p == end) minEndScore = score
            frontier.remove(reindeer)
            for ((newReindeer, scoreDelta) in reindeer.neighbors()) {
                val newScore = score + scoreDelta
                val oldScore = minScores[newReindeer] ?: Int.MAX_VALUE
                if (newScore <= oldScore && newScore <= minEndScore) {  
                    frontier.add(newReindeer)
                    minScores[newReindeer] = newScore
                    val prev = previous.getOrPut(newReindeer) { mutableSetOf<Reindeer>() }
                    if (newScore < oldScore) prev.clear()
                    prev += reindeer
                }
            }
        }
        return minEndScore to previous
    }

    fun countPathPoints(previous: Map<Reindeer, Set<Reindeer>>): Int {
        val frontier = ArrayDeque<Reindeer>(previous.keys.filter { it.p == end })
        val pathPoints = mutableSetOf<Reindeer>()
        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (pathPoints.add(current) && current.p != start) {
                frontier.addAll(previous.getValue(current))
            }
        }
        return pathPoints.map { it.p }.toSet().size
    }
}

fun parseMaze(lines: List<String>): Maze {
    var start: Point? = null
    var end: Point? = null
    val walls = mutableSetOf<Point>()
    for (y in 0..lines.lastIndex) {
        for (x in 0..lines[y].lastIndex) {
            when (lines[y][x]) {
                'S' -> start = Point(x, y)
                'E' -> end = Point(x, y)
                '#' -> walls += Point(x, y)
            }
        }
    }
    return Maze(start!!, walls, end!!)
}

val lines = java.io.File(args[0]).readLines()
val maze = parseMaze(lines)
val (minScore, previous) = maze.findShortestPaths()
println(minScore)
println( maze.countPathPoints(previous))
