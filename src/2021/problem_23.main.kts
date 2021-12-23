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

fun State.move(a: Amphipod, to: Point) = State(
    amphipods.toMutableList().apply {
        remove(a)
        add(a.copy(p = to))
    }.sortedBy { it.hashCode() }, // Need some consistent order for dedupe purposes.
    totalEnergyCost + a.type.cost() * a.p.manhattanDistance(to)
) 

fun State.successors(slotsPerRoom: Int) = sequence {
    fun Amphipod.shouldStayPut() =
        p.x == type.roomX() && amphipods.none { it.p.x == type.roomX() && it.type != type }

    fun Amphipod.canMoveThroughHall(to: Point) =
        (p.x < to.x && amphipods.none { it.p.y == 0 && it.p.x > p.x && it.p.x <= to.x } ||
        (p.x > to.x && amphipods.none { it.p.y == 0 && it.p.x < p.x && it.p.x >= to.x }))

    fun Amphipod.canMoveToHall(to: Point) =
        canMoveThroughHall(to) && amphipods.none { p.x == it.p.x && p.y > it.p.y }

    fun Amphipod.canMoveToRoom(to: Point) = 
        canMoveThroughHall(to) && amphipods.none { it.p.x == type.roomX() && it.type != type }

    amphipods.forEach { a -> when {
        a.shouldStayPut() -> {}
        a.p.y > 0 -> HALLWAY.forEach { if (a.canMoveToHall(it)) yield(move(a, it)) }
        else -> {
            val roomX = a.type.roomX()
            if (a.canMoveToRoom(Point(roomX, 1))) {
                val minOccupiedSlot = amphipods.filter { it.p.x == roomX }.minOfOrNull { it.p.y }
                val availableSlot = minOccupiedSlot?.minus(1) ?: slotsPerRoom
                yield(move(a, Point(roomX, availableSlot)))
            }
        }
    }}
}

fun State.done() = amphipods.none { it.p.x != it.type.roomX() }

val regex = kotlin.text.Regex(".*(A|B|C|D).*(A|B|C|D).*(A|B|C|D).*(A|B|C|D)")
val input = java.io.File(args[0]).readLines()
val map = input.drop(2).dropLast(1).flatMapIndexed { index, value ->
    val (a, b, c, d) = checkNotNull(regex.find(value)).destructured
    listOf(Amphipod(a, Point(2, 1 + index)), Amphipod(b, Point(4, 1 + index)),
           Amphipod(c, Point(6, 1 + index)), Amphipod(d, Point(8, 1 + index)))
}
val slotsPerRoom = map.size / 4

val start = System.nanoTime()
val initialState = State(map, 0)
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
    state.successors(slotsPerRoom).forEach { frontier.add(it) }
}
println("Runtime: ${(System.nanoTime() - start)/1_000_000}ms")
println("States explored: ${seen.size}")
println("Max frontier size: $maxFrontierSize")
