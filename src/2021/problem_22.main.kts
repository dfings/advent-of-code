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

fun IntRange.end() = endInclusive + 1
val sectors = instructions.map { it.sector }
val xs = sectors.flatMap { listOf(it.x.start, it.x.end()) }.sorted().distinct()
val ys = sectors.flatMap { listOf(it.y.start, it.y.end()) }.sorted().distinct()
val zs = sectors.flatMap { listOf(it.z.start, it.z.end()) }.sorted().distinct()

val xIndex = xs.mapIndexed { index, value -> value to index }.toMap()
val yIndex = ys.mapIndexed { index, value -> value to index }.toMap()
val zIndex = zs.mapIndexed { index, value -> value to index }.toMap()

val bits = Array(xs.size) { Array(ys.size) { BooleanArray(zs.size) { false } } }
for (instruction in instructions) {
    val s = instruction.sector
    for (x in xIndex.getValue(s.x.start) until xIndex.getValue(s.x.end())) {
        for (y in yIndex.getValue(s.y.start) until yIndex.getValue(s.y.end())) {
            for (z in zIndex.getValue(s.z.start) until zIndex.getValue(s.z.end())) {
                bits[x][y][z] = instruction.on
            }
        }
    }
}

var volume = 0L
for (x in 0 until xs.lastIndex) {
    for (y in 0 until ys.lastIndex) {
        for (z in 0 until zs.lastIndex) {
            if (bits[x][y][z]) {
                volume += 1L * (xs[x+1]-xs[x]) * (ys[y+1]-ys[y]) * (zs[z+1]-zs[z])
            }
        }
    }
}
println(volume)
