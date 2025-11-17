#!/usr/bin/env kotlin

import kotlin.math.max

data class Point(val x: Int, val y: Int, val z: Int)
data class Brick(val id: Int, val from: Point, val to: Point)

fun Brick.allPoints() = when {
    from.x < to.x -> (from.x..to.x).map { Point(it, from.y, from.z) }.toSet()
    from.y < to.y -> (from.y..to.y).map { Point(from.x, it, from.z) }.toSet()
    from.z < to.z -> (from.z..to.z).map { Point(from.x, from.y, it) }.toSet()
    from == to -> setOf(from)
    else -> throw IllegalStateException("$this")
}

fun Brick.dropTo(zNew: Int): Brick = copy(
    from = from.copy(z = zNew), 
    to = to.copy(z = zNew + (to.z - from.z))
)

class Stack {
    val allBricks = mutableMapOf<Int, Brick>()
    val bricksByPoints = mutableMapOf<Point, Int>()
    val heldBy = mutableMapOf<Int, Set<Int>>()
    val holding = mutableMapOf<Int, MutableSet<Int>>()
    var zMax = 1

    fun getBrick(id: Int) = allBricks.getValue(id)
    fun getBrick(point: Point) = bricksByPoints.get(point)
    fun getHolding(id: Int): Set<Int> = holding[id] ?: emptySet()
    fun getHeldBy(id: Int): Set<Int> = heldBy.getValue(id)

    fun addAll(bricks: List<Brick>) {
        for (brick in bricks.sortedBy { it.from.z }) {
            add(brick)
        }
    }

    fun add(brick: Brick) {
        var z = zMax + 1
        var heldBySet = emptySet<Int>()
        while (z > 1) {
            heldBySet = brick.dropTo(z - 1).allPoints().mapNotNull { getBrick(it) }.toSet()
            if (heldBySet.isNotEmpty()) {
                break
            }
            z--
        }
        val newBrick = brick.dropTo(z)
        zMax = max(zMax, newBrick.to.z)
        allBricks[newBrick.id] = newBrick
        heldBy[newBrick.id] = heldBySet
        for (id in heldBySet) {
            holding.getOrPut(id) { mutableSetOf<Int>() }.add(newBrick.id) 
        }
        for (point in newBrick.allPoints()) {
            bricksByPoints[point] = newBrick.id 
        }
    }

    fun canDisintegrate(id: Int): Boolean {
        return getHolding(id).all { getHeldBy(it).size > 1 }
    }

    fun countFallingBricks(id: Int): Int {
        val fell = mutableSetOf<Int>(id)
        val frontier = mutableSetOf<Int>()
        frontier.addAll(getHolding(id))
        while (!frontier.isEmpty()) {
            val brick = frontier.minBy { getBrick(it).to.z }
            frontier.remove(brick)
            if (getHeldBy(brick).all { it in fell }) {
                fell.add(brick)
                frontier.addAll(getHolding(brick))
            }
        }
        return fell.size - 1
    }
}

fun String.toPoint(): Point {
    val parts = split(",").map { it.toInt() }
    return Point(parts[0], parts[1], parts[2])
}

fun String.toBrick(id: Int): Brick {
    val points = split("~")
    return Brick(id, points[0].toPoint(), points[1].toPoint())
}

fun solve(lines: List<String>) {
    val bricks = lines.mapIndexed { i, it -> it.toBrick(i) }
    val stack = Stack()
    stack.addAll(bricks)
    println(bricks.count { stack.canDisintegrate(it.id) })
    println(bricks.sumOf { stack.countFallingBricks(it.id) })
}

solve(java.io.File(args[0]).readLines())
