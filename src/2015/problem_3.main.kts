#!/usr/bin/env kotlin

enum class Direction(val x: Int, val y: Int, val symbol: Char) {
    NORTH(0, -1, '^'), EAST(1, 0, '<'), SOUTH(0, 1, 'v'), WEST(-1, 0, '>')
}
val symbolTable = Direction.entries.associateBy { it.symbol }

data class Point(val x: Int, val y: Int)
operator fun Point.plus(d: Direction) = Point(x + d.x, y + d.y)

fun visitHouses(instructions: Iterable<Direction>): Set<Point> {
    var position = Point(0, 0)
    val seen = mutableSetOf(position)
    for (direction in instructions) {
        position = position + direction
        seen += position
    }
    return seen
}

val line = java.io.File(args[0]).readLines().single()
val dirs = line.map { symbolTable.getValue(it) }
println(visitHouses(dirs).size)

val santa = dirs.filterIndexed { i, it -> i % 2 == 0 }
val roboSanta = dirs.filterIndexed { i, it -> i % 2 == 1 }
println((visitHouses(santa) union visitHouses(roboSanta)).size)
