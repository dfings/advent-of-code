#!/usr/bin/env kotlin

import java.util.PriorityQueue

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int) { EAST(1, 0), SOUTH(0, 1), WEST(-1, 0), NORTH(0, -1) }
operator fun Point.plus(d: Direction) = Point(x + d.dx, y + d.dy)

val directionCodes = mapOf('^' to Direction.NORTH, 'v' to Direction.SOUTH, '>' to Direction.EAST, '<' to Direction.WEST)

data class Wind(val index: Int, val direction: Direction)

data class Board(
    val winds: Map<Wind, Point>,
    val xMax: Int,
    val yMax: Int) {
    val windPoints = winds.values.toSet()
}

fun Board.next() = copy(
    winds = winds.mapValues { (k, v) -> 
      when (k.direction) {
        Direction.WEST -> v.copy(x = (v.x - 1).mod(xMax + 1))
        Direction.EAST -> v.copy(x = (v.x + 1).mod(xMax + 1))
        Direction.NORTH -> v.copy(y = (v.y - 1).mod(yMax + 1))
        Direction.SOUTH -> v.copy(y = (v.y + 1).mod(yMax + 1))
      }
    }
)

data class State(val current: Point, val steps: Int)
data class Node<T>(val state: T, val cost: Int) : Comparable<Node<T>> {
    override fun compareTo(other: Node<T>) = cost.compareTo(other.cost)
}

fun next(current: Point, board: Board) = buildList<Point> {
    val xMax = board.xMax
    val yMax = board.yMax
    for (next in listOf(current) + Direction.entries.map { current + it }) {
        if (next !in board.windPoints && next.x in 0..xMax && next.y in 0..yMax) add(next)
        if (next.x == 0 && next.y == -1) add(next)
        if (next.x == xMax && next.y == yMax + 1) add(next)
    }
}

fun findShortedPath(startingPoint: Point, boards: List<Board>, endingPoint: Point): Int {
    val upper = boards[0].xMax + boards[0].yMax + 1
    fun State.h() = steps + upper - current.x - current.y

    val frontier = PriorityQueue(listOf(Node(State(startingPoint, 0), upper)))
    val seen = mutableSetOf<State>()

    while (!frontier.isEmpty()) {
        val state = frontier.poll().state
        if (!seen.add(state)) continue
        val (current, distance) = state
        val board = boards[distance]
        if (current == endingPoint) {
            return distance
        }
        val nextBoard = board.next()
        for (next in next(current, nextBoard)) {
            val nextState = State(next, distance + 1)
            frontier.add(Node(nextState, nextState.h()))
        }
    }
    throw IllegalStateException()
}

fun solve(lines: List<String>) {
    val winds = lines.flatMapIndexed { y, line -> line.mapIndexed { x, code -> code to Point(x - 1, y - 1) } }
        .filter { it.first in "<>^v" }
        .mapIndexed { i, it -> Wind(i, directionCodes.getValue(it.first)) to it.second }
        .toMap()
    val start = Point(0, -1)
    val startingBoard = Board(winds, lines[0].lastIndex - 2, lines.lastIndex - 2)
    val boards = generateSequence(startingBoard) { it.next() }.take(1000).toList()
    val end = Point(lines[0].lastIndex - 2, lines.lastIndex - 1)
    val firstLeg = findShortedPath(start, boards, end)
    println(firstLeg)
    val secondLeg = findShortedPath(end, boards.subList(firstLeg, boards.size), start)
    val thirdLeg = findShortedPath(start, boards.subList(firstLeg + secondLeg, boards.size), end)
    println(firstLeg + secondLeg + thirdLeg)
}

solve(java.io.File(args[0]).readLines())
