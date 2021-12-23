#!/usr/bin/env kotlin

import kotlin.math.abs

val regex = kotlin.text.Regex(".*(A|B|C|D).*(A|B|C|D).*(A|B|C|D).*(A|B|C|D)")
val input = java.io.File(args[0]).readLines()

data class Point(val x: Int, val y: Int)
fun Point.manhattanDistance(p: Point): Int = abs(x - p.x) + abs(y - p.y)

val HALLWAY = setOf(Point(0, 0), Point(1, 0), Point(3, 0), Point(5, 0), Point(7, 0), Point(9, 0), Point(10, 0))
val ROOM_A = setOf(Point(2, 1), Point(2, 2))
val ROOM_B = setOf(Point(4, 1), Point(4, 2))
val ROOM_C = setOf(Point(6, 1), Point(6, 2))
val ROOM_D = setOf(Point(8, 1), Point(8, 2))

fun String.room() = when(this) {
    "A" -> ROOM_A
    "B" -> ROOM_B
    "C" -> ROOM_C
    else -> ROOM_D
}

fun String.cost() = when(this) {
    "A" -> 1
    "B" -> 10
    "C" -> 100
    else -> 1000
}

data class Position(val type: String, val loc: Point)
data class State(val positions: Set<Position>)
class Step(val state: State, val energyCost: Int, val totalEnergyCost: Int, val totalEnergyCostEstimate: Int, val last: Step? = null)

fun Step.move(from: Position, to: Position): Step {
    val newState = State(state.positions.toMutableSet().apply {
        remove(from)
        add(to)
    })
    val cost = from.type.cost() * from.loc.manhattanDistance(to.loc)
    return Step(newState, cost, totalEnergyCost + cost, totalEnergyCost + cost + newState.estimateRemainingCost(), this)
}

fun State.estimateRemainingCost(): Int {
    val wrongPlace = positions.filter { it.loc !in it.type.room() }
    return wrongPlace.sumOf { it.loc.manhattanDistance(it.type.room().first()) * it.type.cost() }
}

fun Step.successors(): List<Step> {
    val positions = state.positions
    val occupied = positions.map { it.loc }.toSet()

    fun canMoveThroughHall(from: Point, to: Point) =
        (from.x < to.x && occupied.none { it.y == 0 && it.x > from.x && it.x <= to.x } ||
        (from.x > to.x && occupied.none { it.y == 0 && it.x < from.x && it.x >= to.x }))

    fun canMoveToHall(from: Point, to: Point) =
        canMoveThroughHall(from, to) && occupied.none { from.x == it.x && from.y > it.y }

    fun canMoveToRoom(type: String, from: Point, to: Point) = 
        canMoveThroughHall(from, to) && positions.none { it.loc in type.room() && it.type != type }

    return positions.flatMap { p ->
        if (p.loc.y == 2 && occupied.contains(p.loc.copy(y = 1))) return@flatMap emptyList()
        val homeRoom = p.type.room()
        if (p.loc in homeRoom && positions.none { it !== p && it.loc.x == p.loc.x && it.loc.y > 0 && it.type != p.type }) return@flatMap emptyList()
        buildSet {
            if (p.loc.y > 0) {
                for (h in HALLWAY) {
                    if (canMoveToHall(p.loc, h)) {
                        add(move(p, p.copy(loc = h)))
                    }
                }
            } else {
                val h = homeRoom.first()
                if (canMoveToRoom(p.type, p.loc, h)) {
                    val homeRoomOccupancy = positions.filter { it.loc in homeRoom }
                    if (homeRoomOccupancy.isEmpty()) {
                        add(move(p, p.copy(loc = homeRoom.last())))
                    } else if (homeRoomOccupancy.all { it.type == p.type }) {
                        add(move(p, p.copy(loc = homeRoom.first())))
                    }
                }
            }
        }
    }
}

fun Step.done() = totalEnergyCost == totalEnergyCostEstimate

fun State.render(): String {
    val output = mutableListOf<String>()
    for (y in 0..(positions.maxOf { it.loc.y })) {
        for (x in 0..10) {
            val loc = Point(x, y)
            val p = positions.firstOrNull { it.loc == loc }
            if (p == null) output.add(".") else output.add(p.type)
        }
        output.add("\n")
    }
    return output.joinToString("")
}

val (r11, r21, r31, r41) = checkNotNull(regex.find(input[2])).destructured
val (r12, r22, r32, r42) = checkNotNull(regex.find(input[3])).destructured
val initialState = State(setOf(
    Position(r11, ROOM_A.first()), Position(r12, ROOM_A.last()),
    Position(r21, ROOM_B.first()), Position(r22, ROOM_B.last()),
    Position(r31, ROOM_C.first()), Position(r32, ROOM_C.last()),
    Position(r41, ROOM_D.first()), Position(r42, ROOM_D.last()),
))
val initialStep = Step(initialState, 0, 0, initialState.estimateRemainingCost())

val frontier = mutableSetOf<Step>(initialStep)
val seen = mutableSetOf<State>()
while (!frontier.isEmpty()) {
    val step = frontier.minByOrNull { it.totalEnergyCostEstimate }!!
    frontier.remove(step)
    if (step.state in seen) continue
    seen.add(step.state)
    
    if (step.done()) {
        println(step.state.render())
        println(step.totalEnergyCost)
        break
    }
    frontier.addAll(step.successors())
}
