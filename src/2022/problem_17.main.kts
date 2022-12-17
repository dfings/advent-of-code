#!/usr/bin/env kotlin

import kotlin.math.max

data class Point(val x: Int, val y: Int)
data class Rock(var points: List<Point>)
data class CacheKey(val points: Set<Point>, val rockIndex: Long, val gasIndex: Int)
data class CacheValue(val rockIndex: Long, val yTotal: Long)

fun List<Pair<Int, Int>>.toRock() = Rock(map { Point(it.first, it.second) })

class Chamber(val gas: String) {
    val allPoints = (0..6).map { x -> Point(x, 0) }.toMutableSet()
    var yMax = 0 // Normalized value
    var yTotal = 0L // Total value
    var rockIndex = 0L // Total rocks dropped
    var gasIndex = 0 // Index into gas (modded already)
    val cache = HashMap<CacheKey, CacheValue>()

    fun simulate(rockCount: Long): Long {
        while (rockIndex != rockCount) {
            val rock = newRock()
            do {
                rock.tryMove()
                check(rock.points.all { it.y >= 0 })
            } while (rock.tryFall())
            allPoints.addAll(rock.points)
            yMax = max(yMax, rock.points.maxOf { it.y })
            normalizeChamber()
            val cacheKey = CacheKey(allPoints.toSet(), rockIndex % 5, gasIndex)
            val cacheValue = cache.put(cacheKey, CacheValue(rockIndex, yTotal))
            if (cacheValue != null) {
                val periodicity = (rockIndex - cacheValue.rockIndex)
                val advanceBy = (rockCount - rockIndex) / periodicity
                yTotal += (yTotal - cacheValue.yTotal) * advanceBy
                rockIndex += (advanceBy * periodicity)
            }
            rockIndex++
        }
        return yTotal
    }

    fun newRock(): Rock = when (rockIndex % 5) {
        0L -> listOf(2 to yMax + 4, 3 to yMax + 4, 4 to yMax + 4, 5 to yMax + 4)
        1L -> listOf(2 to yMax + 5, 3 to yMax + 4, 3 to yMax + 5, 3 to yMax + 6, 4 to yMax + 5)
        2L -> listOf(2 to yMax + 4, 3 to yMax + 4, 4 to yMax + 4, 4 to yMax + 5, 4 to yMax + 6)
        3L -> listOf(2 to yMax + 4, 2 to yMax + 5, 2 to yMax + 6, 2 to yMax + 7)
        4L -> listOf(2 to yMax + 4, 2 to yMax + 5, 3 to yMax + 4, 3 to yMax + 5)
        else -> throw IllegalArgumentException()
    }.toRock()

    fun Rock.tryMove() {
        val direction = if (gas[gasIndex] == '<') -1 else 1
        tryUpdate(points.map { Point(it.x + direction, it.y) })
        gasIndex = (gasIndex + 1) % gas.length
    }

    fun Rock.tryFall() = tryUpdate(points.map { Point(it.x, it.y - 1) })

    fun Rock.tryUpdate(newPoints: List<Point>): Boolean {
        if (newPoints.none { it in allPoints || it.x < 0 || it.x > 6 }) {
            points = newPoints
            return true
        }
        return false
    }

    val maxHeight = 64
    fun normalizeChamber() {
        if (yMax <= maxHeight) return
        val newFloor = yMax - maxHeight
        if (yTotal == 0L) yTotal = yMax.toLong() else yTotal += newFloor.toLong()
        yMax = maxHeight
        val remaining = allPoints.filter { it.y >= newFloor }
        allPoints.clear()
        allPoints.addAll(remaining.map { Point(it.x, it.y - newFloor) })
    }
}

fun Chamber.print() {
    for (y in yMax downTo max(0, yMax - 100)) {
        println((0..6).map { x -> if (Point(x, y) in allPoints) "#" else "." }.joinToString(""))
    }
}

val input = java.io.File(args[0]).readLines().single()

val chamber1 = Chamber(input)
println(chamber1.simulate(2022))

val chamber2 = Chamber(input)
println(chamber2.simulate(1000000000000))
