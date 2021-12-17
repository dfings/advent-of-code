#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())
    operator fun plus(p: Point) = Point(x + p.x, y + p.y)
}

data class Vector(val position: Point, val velocity: Point)

enum class State { PENDING, HIT, MISS }

data class Target(val min: Point, val max: Point) {
    fun stateOf(v: Vector) = stateOf(v.position)
    fun stateOf(p: Point) = when {
        p.x > max.x || p.y < min.y -> State.MISS
        p.x < min.x || p.y > max.y -> State.PENDING
        else -> State.HIT
    }
}

val ZERO = Point(0, 0)
val DRAG_XY = Point(-1, -1)
val DRAG_Y = Point(0, -1)
fun Point.fire() = generateSequence(Vector(ZERO, this)) { 
    Vector(
        it.position + it.velocity, 
        if (it.velocity.x > 0) it.velocity + DRAG_XY else it.velocity + DRAG_Y
    )
}

val regex = kotlin.text.Regex("target area: x=(\\d+)..(\\d+), y=(-\\d+)..(-\\d+)")
val input = java.io.File(args[0]).readLines().single()
val (xMin, xMax, yMin, yMax) = checkNotNull(regex.find(input)).destructured
val target = Target(Point(xMin, yMin), Point(xMax, yMax))
val hits = sequence {
    for (x in 1..target.max.x) {
        for (y in target.min.y..-target.min.y) {
            val trajectory = Point(x, y).fire().takeWhile { target.stateOf(it) != State.MISS }
            if (target.stateOf(trajectory.last()) == State.HIT) yield(trajectory)
        }
    }
}

println(hits.maxOf { it.maxOf { it.position.y } })
println(hits.count())
