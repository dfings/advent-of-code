#!/usr/bin/env kotlin

import kotlin.math.max

data class Point(val x: Int, val y: Int)
data class Rock(var points: List<Point>)

fun List<Pair<Int, Int>>.toRock() = Rock(map { Point(it.first, it.second) })

class Chamber(val gas: String) {
    val allPoints = (0..6).map { x -> Point(x, 0) }.toMutableSet()
    var yMax = 0
    var rockIndex = 0
    var gasIndex = 0

    fun simulate(rockCount: Int): Int {
        while (rockIndex != rockCount) {
            val rock = newRock()
            do {
                rock.tryMove()
            } while (rock.tryFall())
            allPoints.addAll(rock.points)
            yMax = max(yMax, rock.points.maxOf { it.y })
            rockIndex++
        }
        return yMax
    }

    fun newRock(): Rock = when(rockIndex % 5) {
        0 -> listOf(2 to yMax + 4, 3 to yMax + 4, 4 to yMax + 4, 5 to yMax + 4)
        1 -> listOf(2 to yMax + 5, 3 to yMax + 4, 3 to yMax + 5, 3 to yMax + 6, 4 to yMax + 5)
        2 -> listOf(2 to yMax + 4, 3 to yMax + 4, 4 to yMax + 4, 4 to yMax + 5, 4 to yMax + 6)
        3 -> listOf(2 to yMax + 4, 2 to yMax + 5, 2 to yMax + 6, 2 to yMax + 7)
        4 -> listOf(2 to yMax + 4, 2 to yMax + 5, 3 to yMax + 4, 3 to yMax + 5)
        else -> throw IllegalArgumentException()
    }.toRock()

    fun Rock.tryMove() {
        val direction = if (gas[gasIndex] == '<') -1 else 1
        if ((direction == -1 && points.all { it.x > 0} ) || (direction == 1 && points.all { it.x < 6 })) {
            val newPoints = points.map { Point(it.x + direction, it.y ) }
            if (newPoints.none { it in allPoints }) {
                points = newPoints
            }
        }
        gasIndex = (gasIndex + 1) % gas.length
    }

    fun Rock.tryFall(): Boolean {
        val newPoints =  points.map { Point(it.x, it.y - 1) }
        if (newPoints.none { it in allPoints }) {
            points = newPoints
            return true
        }
        return false
    }
}

fun Chamber.print() {
    for (y in yMax downTo 0) {
        println((0..6).map { x -> if (Point(x, y) in allPoints) "#" else "." }.joinToString(""))
    }
}

val input = java.io.File(args[0]).readLines().single()
val chamber = Chamber(input)
println(chamber.simulate(2022))
