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
    fun neighbors(r: Reindeer) = buildList {
        if (r.p + r.d !in walls) add(r.copy(p = r.p + r.d) to 1)
        val left = rotateLeft.getValue(r.d)
        if (r.p + left !in walls) add(r.copy(d = left) to 1000)
        val right = rotateRight.getValue(r.d)
        if (r.p + right !in walls) add(r.copy(d = right) to 1000)
    }

    fun findShortestPath(): Pair<Int, Map<Reindeer, Set<Reindeer>>> {
        val minScores = mutableMapOf<Reindeer, Int>()
        val previous = mutableMapOf<Reindeer, MutableSet<Reindeer>>()
        var minEndScore = Int.MAX_VALUE
        val frontier = mutableSetOf(Reindeer(start, Direction.EAST) to 0)
        while (!frontier.isEmpty()) {
            val (reindeer, score) = frontier.minBy { it.second }
            if (reindeer.p == end && score < minEndScore) minEndScore = score
            frontier.remove(reindeer to score)
            for ((newReindeer, scoreDelta) in neighbors(reindeer)) {
                val newScore = score + scoreDelta
                val oldScore = minScores.get(newReindeer) ?: Int.MAX_VALUE
                if (newScore <= oldScore && newScore <= minEndScore) {                        
                    frontier.add(newReindeer to newScore)
                    minScores[newReindeer] = newScore
                    val prev = previous.getOrPut(newReindeer) { mutableSetOf<Reindeer>() }
                    if (newScore < oldScore) prev.clear()
                    prev += reindeer
                }
            }
        }
        return minEndScore to previous
    }

    fun countPathPoints(previous: Map<Reindeer, Set<Reindeer>>): Set<Point> {
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
val (minScore, previous) = maze.findShortestPath()
println(minScore)
val pathPoints = maze.countPathPoints(previous)
println(pathPoints.size)
