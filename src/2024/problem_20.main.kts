#!/usr/bin/env kotlin

import kotlin.math.abs

enum class Direction(val x: Int, val y: Int) {
    NORTH(0, -1), EAST(1, 0), SOUTH(0, 1), WEST(-1, 0)
}

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

data class Track(val start: Point, val walls: Set<Point>, val end: Point) {
    fun findPath() = buildList {
        var current = start
        add(start)
        while (current != end) {
            current = Direction.entries.map { current + it }
                .filter { it !in walls && it != getOrNull(lastIndex - 1) }
                .single()
            add(current)
        }
    }
}
   
fun analyzeCheats(path: List<Point>, pathIndexes: Map<Point, Int>, cheats: Int, target: Int): Int {
    val effectiveCheats = mutableMapOf<Pair<Point, Point>, Int>()
    for ((cheatStartIndex, cheatStart) in path.subList(0, path.lastIndex - 100).withIndex()) {
        for (yDelta in -cheats..cheats) {
            val absYDelta = abs(yDelta)
            for (xDelta in (-cheats + absYDelta)..(cheats - absYDelta)) {
                val cheatEnd = Point(cheatStart.x + xDelta, cheatStart.y + yDelta)
                val cheatEndIndex = pathIndexes[cheatEnd]
                if (cheatEndIndex == null) continue
                
                val cheatLength = abs(xDelta) + absYDelta
                val savings = cheatEndIndex - cheatStartIndex - cheatLength
                if (savings < target) continue
                        
                effectiveCheats.put(cheatStart to cheatEnd, savings)
            }
        }
    }
    return effectiveCheats.size
}


fun parseTrack(lines: List<String>): Track {
    val pointToChar = lines.flatMapIndexed { y, line -> 
        line.mapIndexed { x, it -> Point(x, y) to it }
    }
    return Track(
        pointToChar.single { it.second == 'S'}.first,
        pointToChar.filter { it.second == '#' }.map { it.first }.toSet(),
        pointToChar.single { it.second == 'E'}.first,
    )
}

val lines = java.io.File(args[0]).readLines()
val track = parseTrack(lines)
val path = track.findPath()
val pathIndexes = path.mapIndexed { i, it -> it to i }.toMap()
println(analyzeCheats(path, pathIndexes, 2, 100))
println(analyzeCheats(path, pathIndexes, 20, 100))
