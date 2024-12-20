#!/usr/bin/env kotlin

import kotlin.math.abs
import kotlin.time.measureTime

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

fun analyzeCheats(path: List<Point>, maxCheatLength: Int, target: Int) =
    path.subList(0, path.size - target).flatMapIndexed { startIndex, start ->
        path.subList(startIndex + target, path.size).mapIndexedNotNull { endOffset, end ->
            val endIndex = startIndex + target + endOffset
            val cheatLength = abs(start.x - end.x) + abs(start.y - end.y)
            val savings = endIndex - startIndex - cheatLength
            if (cheatLength <= maxCheatLength && savings >= target) start to end else null
        }
    }.distinct().size


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
println(analyzeCheats(path, 2, 100))
println(analyzeCheats(path, 20, 100))
