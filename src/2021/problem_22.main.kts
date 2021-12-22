#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

data class Point(val x: Int, val y: Int, val z: Int)
data class Sector(val x: IntRange, val y: IntRange, val z: IntRange) {
    fun contains(p: Point) = p.x in x && p.y in y && p.z in z
}
data class Instruction(val on: Boolean, val sector: Sector)

fun range(start: String, endInclusive: String): IntRange = start.toInt()..endInclusive.toInt()
val regex = kotlin.text.Regex("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
val input = java.io.File(args[0]).readLines()
val instructions = input.map {
    val (onOff, xMin, xMax, yMin, yMax, zMin, zMax) = checkNotNull(regex.find(it)).destructured
    Instruction(onOff == "on", Sector(range(xMin, xMax), range(yMin, yMax), range(zMin, zMax)))
}

val states = buildMap {
    for (x in -50..50) for (y in -50..50) for (z in -50..50) put(Point(x, y, z), false)
}.toMutableMap()
instructions.forEach { instruction ->
    val toUpdate = states.keys.filter { instruction.sector.contains(it) }
    toUpdate.forEach { states.put(it, instruction.on) }
}
println(states.values.count { it })
