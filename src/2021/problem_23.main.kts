#!/usr/bin/env kotlin

import kotlin.math.abs

enum class Type(val code: String, val roomX: Int, val cost: Int) {
    AMBER("A", 2, 1),
    BRONZE("B", 4, 10),
    COPPER("C", 6, 100),
    DESERT("D", 8, 1000),
}

data class Point(val x: Int, val y: Int)
data class Amphipod(val type: Type, val p: Point)
data class State(val amphipods: List<Amphipod>, val totalEnergyCost: Int)

fun Point.manhattanDistance(p: Point): Int = abs(x - p.x) + abs(y - p.y)

val HALLWAY = listOf(Point(0, 0), Point(1, 0), Point(3, 0), Point(5, 0), Point(7, 0), Point(9, 0), Point(10, 0))

fun State.move(a: Amphipod, to: Point) = State(
    amphipods.toMutableList().apply {
        set(indexOf(a), a.copy(p = to))
        sortBy { it.hashCode() } // Need some consistent order for dedupe purposes.
    },
    totalEnergyCost + a.type.cost * a.p.manhattanDistance(to)
) 

fun State.successors(slotsPerRoom: Int) = sequence {
    fun Amphipod.roomOnlyHasCorrectTypes() = 
        amphipods.none { it.p.x == type.roomX && it.type != type }

    fun Amphipod.shouldStayPut() =
        p.x == type.roomX && roomOnlyHasCorrectTypes()

    fun Amphipod.canMoveThroughHall(x: Int) =
        (p.x < x && amphipods.none { it.p.y == 0 && it.p.x > p.x && it.p.x <= x } ||
        (p.x > x && amphipods.none { it.p.y == 0 && it.p.x < p.x && it.p.x >= x }))

    fun Amphipod.canMoveToHall() =
        p.y > 0 && amphipods.none { p.x == it.p.x && p.y > it.p.y }

    fun Amphipod.canMoveToRoom() = 
        p.y == 0 && canMoveThroughHall(type.roomX) && roomOnlyHasCorrectTypes()

    amphipods.forEach { a -> when {
        a.shouldStayPut() -> {}
        a.canMoveToHall() ->  HALLWAY.forEach { if (a.canMoveThroughHall(it.x)) yield(move(a, it)) }
        a.canMoveToRoom() -> {
            val minOccupiedSlot = amphipods.filter { it.p.x == a.type.roomX }.minOfOrNull { it.p.y } ?: slotsPerRoom + 1
            yield(move(a, Point(a.type.roomX, minOccupiedSlot - 1)))
        }
        else -> {}
    }}
}

fun State.done() = amphipods.none { it.p.x != it.type.roomX }

fun String.toType() = Type.values().single { this == it.code }

data class Solution(val totalEnergyCost: Int, val statesExplored: Int, val maxFrontierSize: Int)
fun solve(initialState: State): Solution {
    val slotsPerRoom = initialState.amphipods.size / 4
    val frontier = java.util.PriorityQueue<State>() { 
        a, b -> a.totalEnergyCost.compareTo(b.totalEnergyCost) 
    }
    frontier.add(initialState)
    val seen = HashSet<List<Amphipod>>()
    var maxFrontierSize = 0
    while (!frontier.isEmpty()) {
        if (frontier.size > maxFrontierSize) maxFrontierSize = frontier.size
        val state = frontier.poll()
        when {
            state.amphipods in seen -> {}
            state.done() -> return Solution(state.totalEnergyCost, seen.size, maxFrontierSize)
            else -> {   
                seen.add(state.amphipods)
                state.successors(slotsPerRoom).forEach { frontier.add(it) }
            }
        }
    }
    error("No solution!")
}

val regex = kotlin.text.Regex(".*(A|B|C|D).*(A|B|C|D).*(A|B|C|D).*(A|B|C|D)")
val input = java.io.File(args[0]).readLines()
val amphipods = input.drop(2).dropLast(1).flatMapIndexed { index, value ->
    val (a, b, c, d) = checkNotNull(regex.find(value)).destructured
    listOf(Amphipod(a.toType(), Point(2, 1 + index)), Amphipod(b.toType(), Point(4, 1 + index)),
           Amphipod(c.toType(), Point(6, 1 + index)), Amphipod(d.toType(), Point(8, 1 + index)))
}

val start = System.nanoTime()
val solution = solve(State(amphipods, 0))
println("Total energy cost: ${solution.totalEnergyCost}")
println("States explored: ${solution.statesExplored}")
println("Max frontier size: ${solution.maxFrontierSize}")
println("Runtime: ${(System.nanoTime() - start)/1_000_000}ms")
