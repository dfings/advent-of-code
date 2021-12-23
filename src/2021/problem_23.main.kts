#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class Position(val type: String, val loc: Point)
data class State(val positions: List<Position>, val totalEnergyCost: Int)

fun Point.manhattanDistance(p: Point): Int = abs(x - p.x) + abs(y - p.y)

val HALLWAY = setOf(Point(0, 0), Point(1, 0), Point(3, 0), Point(5, 0), Point(7, 0), Point(9, 0), Point(10, 0))

fun String.cost() = when(this) {
    "A" -> 1
    "B" -> 10
    "C" -> 100
    else -> 1000
}
fun String.roomColumn() = when(this) {
    "A" -> 2
    "B" -> 4
    "C" -> 6
    else -> 8
}

fun State.move(from: Position, to: Position): State {
    val cost = from.type.cost() * from.loc.manhattanDistance(to.loc)
    return State(
        positions.toMutableList().apply {
            remove(from)
            add(to)
        }.sortedBy { it.hashCode() }, // Need some consistent order for dedupe purposes.
        totalEnergyCost + cost
    ) 
}

fun State.successors(room: String.() -> Set<Point>): List<State> {
    fun shouldStayPut(p: Position) =
        p.loc.x == p.type.roomColumn() && positions.none { it.loc.x == p.type.roomColumn() && it.type != p.type }

    fun canMoveThroughHall(from: Point, to: Point) =
        (from.x < to.x && positions.none { it.loc.y == 0 && it.loc.x > from.x && it.loc.x <= to.x } ||
        (from.x > to.x && positions.none { it.loc.y == 0 && it.loc.x < from.x && it.loc.x >= to.x }))

    fun canMoveToHall(from: Point, to: Point) =
        canMoveThroughHall(from, to) && positions.none { from.x == it.loc.x && from.y > it.loc.y }

    fun canMoveToRoom(type: String, from: Point, to: Point) = 
        canMoveThroughHall(from, to) && positions.none { it.loc.x == type.roomColumn() && it.type != type }

    return positions.flatMap { p ->
        if (shouldStayPut(p)) return@flatMap emptyList()
        buildList {
            if (p.loc.y > 0) {
                for (h in HALLWAY) {
                    if (canMoveToHall(p.loc, h)) {
                        add(move(p, p.copy(loc = h)))
                    }
                }
            } else {
                val homeRoom = p.type.room()
                val h = homeRoom.first()
                if (canMoveToRoom(p.type, p.loc, h)) {
                    val minOccupiedSlot = positions.filter { it.loc.x == h.x }.minOfOrNull { it.loc.y }
                    val availableSlot = minOccupiedSlot?.minus(1) ?: homeRoom.maxOf { it.y }
                    add(move(p, p.copy(loc = h.copy(y = availableSlot))))
                }
            }
        }
    }
}

fun State.done() = positions.none { it.loc.x != it.type.roomColumn() }

val regex = kotlin.text.Regex(".*(A|B|C|D).*(A|B|C|D).*(A|B|C|D).*(A|B|C|D)")
val input = java.io.File(args[0]).readLines()
val map = mutableListOf<Position>()
val rooms = Array(4) { mutableSetOf<Point>() }
input.drop(2).dropLast(1).forEachIndexed { index, value ->
    rooms[0].add(Point(2, 1 + index))
    rooms[1].add(Point(4, 1 + index))
    rooms[2].add(Point(6, 1 + index))
    rooms[3].add(Point(8, 1 + index))
    val (a, b, c, d) = checkNotNull(regex.find(value)).destructured
    map.add(Position(a, rooms[0].last()))
    map.add(Position(b, rooms[1].last()))
    map.add(Position(c, rooms[2].last()))
    map.add(Position(d, rooms[3].last()))
}

val initialState = State(map, 0)
val start = System.nanoTime()
val frontier = java.util.PriorityQueue<State>() { 
    a: State, b: State -> a.totalEnergyCost.compareTo(b.totalEnergyCost) 
}
frontier.add(initialState)
val seen = mutableSetOf<List<Position>>()
var maxFrontierSize = 0
while (!frontier.isEmpty()) {
    if (frontier.size > maxFrontierSize) maxFrontierSize = frontier.size
    val state = frontier.poll()
    if (state.positions in seen) continue
    seen.add(state.positions)
    
    if (state.done()) {
        println("Total energy cost: ${state.totalEnergyCost}")
        break
    }
    frontier.addAll(state.successors {
        when(this) {
            "A" -> rooms[0]
            "B" -> rooms[1]
            "C" -> rooms[2]
            else -> rooms[3]
        }
    })
}
println("Runtime: ${(System.nanoTime() - start)/1_000_000}ms")
println("States explored: ${seen.size}")
println("Max frontier size: $maxFrontierSize")
