#!/usr/bin/env kotlin

import kotlin.math.abs

fun manhattanDistance(x1: Int, y1: Int, x2: Int, y2: Int) = abs(x1 - x2) + abs(y1 - y2)
fun manhattanDistance(p1: Point, p2: Point) = manhattanDistance(p1.x, p1.y, p2.x, p2.y)

data class Point(val x: Int, val y: Int)
data class Sensor(val loc: Point, val beacon: Point) {
    val distance = manhattanDistance(loc, beacon)
    fun inRange(x: Int, y: Int) = manhattanDistance(loc.x, loc.y, x, y) <= distance
    fun isBeacon(x: Int, y: Int) = beacon.x == x && beacon.y == y
    fun noBeacon(x: Int, y: Int) = inRange(x, y) && !isBeacon(x, y)
}

val pattern = Regex(".*x=(-?\\d+), y=(-?\\d+).*x=(-?\\d+), y=(-?\\d+)")
val lines = java.io.File(args[0]).readLines()
val sensors = lines.map { line ->
    val (x1, y1, x2, y2) = pattern.find(line)!!.destructured
    Sensor(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
}

val xMin = sensors.minOf { it.loc.x - it.distance }
val xMax = sensors.maxOf { it.loc.x + it.distance }
val fixedDepth = 2000000
println((xMin..xMax).count { x -> sensors.any { it.noBeacon(x, fixedDepth)} })

val beaconMax = 4000000
fun findBeacon(): Point? {
    for (x in 0..beaconMax) {
        var y = 0
        while (y <= beaconMax) {
            val sensor = sensors.find { it.inRange(x, y) }
            if (sensor == null) return Point(x, y)
            y = sensor.loc.y + sensor.distance - abs(x - sensor.loc.x) + 1
        }
    }
    return null
}
val beacon = findBeacon()!!
println(beacon.x.toLong() * 4000000L + beacon.y.toLong())
