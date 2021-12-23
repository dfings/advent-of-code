#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Int, val y: Int)
fun Point.manhattanDistance(p: Point): Int = abs(x - p.x) + abs(y - p.y)

val HALLWAY = setOf(Point(0, 0), Point(1, 0), Point(3, 0), Point(5, 0), Point(7, 0), Point(9, 0), Point(10, 0))
val ROOM_A = setOf(Point(2, 1), Point(2, 2))
val ROOM_B = setOf(Point(4, 1), Point(4, 2))
val ROOM_C = setOf(Point(6, 1), Point(6, 2))
val ROOM_D = setOf(Point(8, 1), Point(8, 2))

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
    val wrongPlace = positions.filter { it.loc.x != it.type.roomColumn() }
    return wrongPlace.sumOf { it.loc.manhattanDistance(Point(it.type.roomColumn(), 1)) * it.type.cost() }
}

fun Step.successors(room: String.() -> Set<Point>): List<Step> {
    val positions = state.positions
    val occupied = positions.map { it.loc }.toSet()

    fun shouldStayPut(p: Position) =
        p.loc in p.type.room() && positions.none { it.loc in p.type.room() && it.type != p.type }

    fun canMoveThroughHall(from: Point, to: Point) =
        (from.x < to.x && occupied.none { it.y == 0 && it.x > from.x && it.x <= to.x } ||
        (from.x > to.x && occupied.none { it.y == 0 && it.x < from.x && it.x >= to.x }))

    fun canMoveToHall(from: Point, to: Point) =
        canMoveThroughHall(from, to) && occupied.none { from.x == it.x && from.y > it.y }

    fun canMoveToRoom(type: String, from: Point, to: Point) = 
        canMoveThroughHall(from, to) && positions.none { it.loc in type.room() && it.type != type }

    return positions.flatMap { p ->
        if (shouldStayPut(p)) return@flatMap emptySet()
        buildSet {
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
                    val availableSlot = (occupied intersect homeRoom).minOfOrNull { it.y }?.minus(1) ?: homeRoom.maxOf { it.y}
                    add(move(p, p.copy(loc = h.copy(y = availableSlot))))
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

val regex = kotlin.text.Regex(".*(A|B|C|D).*(A|B|C|D).*(A|B|C|D).*(A|B|C|D)")
val input = java.io.File(args[0]).readLines()
val map = mutableSetOf<Position>()
val rooms = Array(4) { mutableSetOf<Point>() }
input.drop(2).dropLast(1).forEachIndexed { index, value ->
    val (a, b, c, d) = checkNotNull(regex.find(value)).destructured
    map.add(Position(a, Point(2, 1 + index)))
    map.add(Position(b, Point(4, 1 + index)))
    map.add(Position(c, Point(6, 1 + index)))
    map.add(Position(d, Point(8, 1 + index)))
    rooms[0].add(Point(2, 1 + index))
    rooms[1].add(Point(4, 1 + index))
    rooms[2].add(Point(6, 1 + index))
    rooms[3].add(Point(8, 1 + index))
}

val initialState = State(map)
val initialStep = Step(initialState, 0, 0, initialState.estimateRemainingCost())

val frontier = mutableSetOf<Step>(initialStep)
val seen = mutableSetOf<State>()
while (!frontier.isEmpty()) {
    val step = frontier.minByOrNull { it.totalEnergyCostEstimate }!!
    frontier.remove(step)
    if (step.state in seen) continue
    seen.add(step.state)
    
    if (step.done()) {
        println(step.totalEnergyCost)
        break
    }
    frontier.addAll(step.successors {
        when(this) {
            "A" -> rooms[0]
            "B" -> rooms[1]
            "C" -> rooms[2]
            else -> rooms[3]
        }
    })
}
