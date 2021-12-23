#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Int, val y: Int)
data class Amphipod(val type: String, val p: Point)
data class State(val amphipods: List<Amphipod>, val totalEnergyCost: Int)

fun Point.manhattanDistance(p: Point): Int = abs(x - p.x) + abs(y - p.y)

val HALLWAY = setOf(Point(0, 0), Point(1, 0), Point(3, 0), Point(5, 0), Point(7, 0), Point(9, 0), Point(10, 0))

fun String.cost() = when(this) {
    "A" -> 1
    "B" -> 10
    "C" -> 100
    else -> 1000
}

fun String.roomX() = when(this) {
    "A" -> 2
    "B" -> 4
    "C" -> 6
    else -> 8
}

fun State.move(from: Amphipod, to: Point) = State(
    amphipods.toMutableList().apply {
        remove(from)
        add(from.copy(p = to))
    }.sortedBy { it.hashCode() }, // Need some consistent order for dedupe purposes.
    totalEnergyCost + from.type.cost() * from.p.manhattanDistance(to)
) 

fun State.successors(slotsPerRoom: Int): List<State> {
    fun shouldStayPut(a: Amphipod) =
        a.p.x == a.type.roomX() && amphipods.none { it.p.x == a.type.roomX() && it.type != a.type }

    fun canMoveThroughHall(from: Point, to: Point) =
        (from.x < to.x && amphipods.none { it.p.y == 0 && it.p.x > from.x && it.p.x <= to.x } ||
        (from.x > to.x && amphipods.none { it.p.y == 0 && it.p.x < from.x && it.p.x >= to.x }))

    fun canMoveToHall(from: Point, to: Point) =
        canMoveThroughHall(from, to) && amphipods.none { from.x == it.p.x && from.y > it.p.y }

    fun canMoveToRoom(type: String, from: Point, to: Point) = 
        canMoveThroughHall(from, to) && amphipods.none { it.p.x == type.roomX() && it.type != type }

    return amphipods.flatMap { a ->
        if (shouldStayPut(a)) return@flatMap emptyList()
        buildList {
            if (a.p.y > 0) {
                for (h in HALLWAY) {
                    if (canMoveToHall(a.p, h)) {
                        add(move(a, h))
                    }
                }
            } else {
                val roomX = a.type.roomX()
                if (canMoveToRoom(a.type, a.p, Point(roomX, 1))) {
                    val minOccupiedSlot = amphipods.filter { it.p.x == roomX }.minOfOrNull { it.p.y }
                    val availableSlot = minOccupiedSlot?.minus(1) ?: slotsPerRoom
                    add(move(a, Point(roomX, availableSlot)))
                }
            }
        }
    }
}

fun State.done() = amphipods.none { it.p.x != it.type.roomX() }

val regex = kotlin.text.Regex(".*(A|B|C|D).*(A|B|C|D).*(A|B|C|D).*(A|B|C|D)")
val input = java.io.File(args[0]).readLines()
val map = mutableListOf<Amphipod>()
input.drop(2).dropLast(1).forEachIndexed { index, value ->
    val (a, b, c, d) = checkNotNull(regex.find(value)).destructured
    map.add(Amphipod(a, Point(2, 1 + index)))
    map.add(Amphipod(b, Point(4, 1 + index)))
    map.add(Amphipod(c, Point(6, 1 + index)))
    map.add(Amphipod(d, Point(8, 1 + index)))
}
val slotsPerRoom = map.size / 4

val initialState = State(map, 0)
val start = System.nanoTime()
val frontier = java.util.PriorityQueue<State>() { 
    a: State, b: State -> a.totalEnergyCost.compareTo(b.totalEnergyCost) 
}
frontier.add(initialState)
val seen = mutableSetOf<List<Amphipod>>()
var maxFrontierSize = 0
while (!frontier.isEmpty()) {
    if (frontier.size > maxFrontierSize) maxFrontierSize = frontier.size
    val state = frontier.poll()
    if (state.amphipods in seen) continue
    seen.add(state.amphipods)
    
    if (state.done()) {
        println("Total energy cost: ${state.totalEnergyCost}")
        break
    }
    frontier.addAll(state.successors(slotsPerRoom))
}
println("Runtime: ${(System.nanoTime() - start)/1_000_000}ms")
println("States explored: ${seen.size}")
println("Max frontier size: $maxFrontierSize")
