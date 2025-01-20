#!/usr/bin/env kotlin

val gridSize = 100

data class Point(val x: Int, val y: Int)
fun Point.neighbors() = listOf(
    Point(x - 1, y + 1), Point(x, y + 1), Point(x + 1, y + 1),
    Point(x - 1, y),                      Point(x + 1, y),
    Point(x - 1, y - 1), Point(x, y - 1), Point(x + 1, y - 1),
).filter { it.x in 0..<gridSize && it.y in 0..<gridSize }
fun Point.isCorner() = (x == 0 || x == gridSize - 1) && (y == 0 || y == gridSize - 1)

typealias State = Map<Point, Boolean>
fun State.next(stuck: Boolean = false): State = mapValues{ (k, v) ->
    when {
        stuck && k.isCorner() -> true
        v -> k.neighbors().count { getValue(it) } in 2..3
        else -> k.neighbors().count { getValue(it) } == 3
    }
}

val lines = java.io.File(args[0]).readLines()
var initialState = lines.flatMapIndexed { y, line -> 
    line.mapIndexed { x, char -> Point(x, y) to (char == '#') }
}.toMap()

var state = initialState
var state2 = initialState.mapValues { (k, v) -> k.isCorner() || v }
repeat (100) {
    state = state.next()
    state2 = state2.next(true)
}
println(state.values.count { it })
println(state2.values.count { it })
