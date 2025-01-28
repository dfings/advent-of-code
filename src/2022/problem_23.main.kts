#!/usr/bin/env kotlin

import java.util.EnumMap

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int) { 
    NORTH(0, -1), SOUTH(0, 1), WEST(-1, 0), EAST(1, 0), 
    NORTH_EAST(1, -1),SOUTH_EAST(1, 1), SOUTH_WEST(-1, 1),  NORTH_WEST(-1, -1),
}
operator fun Point.plus(d: Direction) = Point(x + d.dx, y + d.dy)

data class State(val elves: Set<Point>, val round: Int)

fun State.tryPropose(elf: Point, next: Map<Direction, Point>, tryCount: Int): Boolean = when((tryCount + round) % 4) {
    0 -> next[Direction.NORTH] !in elves && next[Direction.NORTH_WEST] !in elves && next[Direction.NORTH_EAST] !in elves
    1 -> next[Direction.SOUTH] !in elves && next[Direction.SOUTH_WEST] !in elves && next[Direction.SOUTH_EAST] !in elves
    2 -> next[Direction.WEST] !in elves && next[Direction.NORTH_WEST] !in elves && next[Direction.SOUTH_WEST] !in elves
    else -> next[Direction.EAST] !in elves && next[Direction.NORTH_EAST] !in elves && next[Direction.SOUTH_EAST] !in elves
}

fun State.propose(elf: Point): Point {
    val next = Direction.entries.map { it to elf + it }.toMap(EnumMap(Direction::class.java))
    if (next.values.all { it !in elves }) return elf
    for (i in 0..3) {
        if (tryPropose(elf, next, i)) {
            return next.getValue(Direction.entries[(i + round) % 4])
        }
    }
    return elf
}

fun State.next(): State {
    val proposedMoves = elves.map { it to propose(it) }
    val moveCounts = proposedMoves.groupingBy { it.second }.eachCount()
    val newElves = proposedMoves.map { if (moveCounts[it.second] == 1) it.second else it.first }
    return State(newElves.toSet(), round + 1)
}

fun solve(lines: List<String>) {
    val elves = lines.flatMapIndexed { y, line -> line.mapIndexed { x, code -> code to Point(x, y) } }
        .filter { it.first == '#' }
        .map { it.second }
        .toSet()
    var state = State(elves, 0)
    repeat(10) {
        state = state.next()
    }
    val xMin = state.elves.minOf { it.x }
    val xMax = state.elves.maxOf { it.x }
    val yMin = state.elves.minOf { it.y }
    val yMax = state.elves.maxOf { it.y }
    var count = 0
    for (y in yMin..yMax) {
        for (x in xMin..xMax) {
            if (Point(x, y) !in state.elves) count++
        }
    }
    println(count)
    var previous: State? = null
    while (previous?.elves != state.elves) {
        previous = state
        state = state.next()
    }
    println(state.round)
}

solve(java.io.File(args[0]).readLines())
