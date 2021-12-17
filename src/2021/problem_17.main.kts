#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())
    operator fun plus(p: Point) = Point(x + p.x, y + p.y)
    operator fun minus(p: Point) = Point(x - p.x, y - p.y)
}

val ZERO = Point(0, 0)
val DRAG_XY = Point(1, 1)
val DRAG_Y = Point(0, 1)

data class Vector(val position: Point, val velocity: Point)
fun Vector.next() = Vector(position + velocity, if (velocity.x > 0) velocity - DRAG_XY else velocity - DRAG_Y)

enum class State { PENDING, HIT, MISS }

data class Target(val min: Point, val max: Point) {
    fun stateOf(p: Point) = when {
        p.x > max.x || p.y < min.y -> State.MISS
        p.x < min.x || p.y > max.y -> State.PENDING
        else -> State.HIT
    }
}

fun Point.fire(): Sequence<Vector> = generateSequence(Vector(ZERO, this)) { it.next() }

val regex = kotlin.text.Regex("target area: x=(-?\\d+)..(-?\\d+), y=(-?\\d+)..(-?\\d+)")
val input = java.io.File(args[0]).readLines().single()
val match = regex.find(input)!!
val (xMin, xMax, yMin, yMax) = match.destructured
val target = Target(Point(xMin, yMin), Point(xMax, yMax))
println(target)

val candidates = sequence {
    for (x in 1..target.max.x) {
        for (y in target.min.y..2000) {
            yield(Point(x, y))
        }
    }
}

val bestY = candidates.maxOf {
    val trajectory = it.fire().takeWhile { target.stateOf(it.position) != State.MISS }
    val lastPosition = trajectory.last().position
    if (target.stateOf(lastPosition) == State.HIT) trajectory.maxOf { it.position.y } else -1
}
println(bestY)

val totalVelocities = candidates.sumOf {
    val trajectory = it.fire().takeWhile { target.stateOf(it.position) != State.MISS }
    val lastPosition = trajectory.last().position
    if (target.stateOf(lastPosition) == State.HIT) 1L else 0L
}
println(totalVelocities)
