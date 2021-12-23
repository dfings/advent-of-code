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
fun String.roomColumn() = when(this) {
    "A" -> 2
    "B" -> 4
    "C" -> 6
    else -> 8
}

fun State.move(from: Amphipod, to: Point): State {
    val cost = from.type.cost() * from.p.manhattanDistance(to)
    return State(
        amphipods.toMutableList().apply {
            remove(from)
            add(from.copy(p = to))
        }.sortedBy { it.hashCode() }, // Need some consistent order for dedupe purposes.
        totalEnergyCost + cost
    ) 
}

fun State.successors(room: String.() -> Set<Point>): List<State> {
    fun shouldStayPut(p: Amphipod) =
        p.p.x == p.type.roomColumn() && amphipods.none { it.p.x == p.type.roomColumn() && it.type != p.type }

    fun canMoveThroughHall(from: Point, to: Point) =
        (from.x < to.x && amphipods.none { it.p.y == 0 && it.p.x > from.x && it.p.x <= to.x } ||
        (from.x > to.x && amphipods.none { it.p.y == 0 && it.p.x < from.x && it.p.x >= to.x }))

    fun canMoveToHall(from: Point, to: Point) =
        canMoveThroughHall(from, to) && amphipods.none { from.x == it.p.x && from.y > it.p.y }

    fun canMoveToRoom(type: String, from: Point, to: Point) = 
        canMoveThroughHall(from, to) && amphipods.none { it.p.x == type.roomColumn() && it.type != type }

    return amphipods.flatMap { p ->
        if (shouldStayPut(p)) return@flatMap emptyList()
        buildList {
            if (p.p.y > 0) {
                for (h in HALLWAY) {
                    if (canMoveToHall(p.p, h)) {
                        add(move(p, h))
                    }
                }
            } else {
                val homeRoom = p.type.room()
                val h = homeRoom.first()
                if (canMoveToRoom(p.type, p.p, h)) {
                    val minOccupiedSlot = amphipods.filter { it.p.x == h.x }.minOfOrNull { it.p.y }
                    val availableSlot = minOccupiedSlot?.minus(1) ?: homeRoom.maxOf { it.y }
                    add(move(p, h.copy(y = availableSlot)))
                }
            }
        }
    }
}

fun State.done() = amphipods.none { it.p.x != it.type.roomColumn() }

val regex = kotlin.text.Regex(".*(A|B|C|D).*(A|B|C|D).*(A|B|C|D).*(A|B|C|D)")
val input = java.io.File(args[0]).readLines()
val map = mutableListOf<Amphipod>()
val rooms = Array(4) { mutableSetOf<Point>() }
input.drop(2).dropLast(1).forEachIndexed { index, value ->
    rooms[0].add(Point(2, 1 + index))
    rooms[1].add(Point(4, 1 + index))
    rooms[2].add(Point(6, 1 + index))
    rooms[3].add(Point(8, 1 + index))
    val (a, b, c, d) = checkNotNull(regex.find(value)).destructured
    map.add(Amphipod(a, rooms[0].last()))
    map.add(Amphipod(b, rooms[1].last()))
    map.add(Amphipod(c, rooms[2].last()))
    map.add(Amphipod(d, rooms[3].last()))
}

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
