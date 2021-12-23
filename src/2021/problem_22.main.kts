#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int, val z: Int)
data class Sector(val x: IntRange, val y: IntRange, val z: IntRange)
data class Instruction(val on: Boolean, val sector: Sector)

fun parseIntRange(start: String, endInclusive: String): IntRange = start.toInt()..endInclusive.toInt()
val regex = kotlin.text.Regex("(on|off) x=(-?\\d+)..(-?\\d+),y=(-?\\d+)..(-?\\d+),z=(-?\\d+)..(-?\\d+)")
val input = java.io.File(args[0]).readLines()
val instructions = input.map {
    val (onOff, xMin, xMax, yMin, yMax, zMin, zMax) = checkNotNull(regex.find(it)).destructured
    Instruction(onOff == "on", Sector(parseIntRange(xMin, xMax), parseIntRange(yMin, yMax), parseIntRange(zMin, zMax)))
}

// Part 1
fun Sector.contains(p: Point) = p.x in x && p.y in y && p.z in z
val states = buildMap {
    for (x in -50..50) for (y in -50..50) for (z in -50..50) put(Point(x, y, z), false)
}.toMutableMap()
instructions.forEach { instruction ->
    val toUpdate = states.keys.filter { instruction.sector.contains(it) }
    toUpdate.forEach { states.put(it, instruction.on) }
}
println(states.values.count { it })

// Part 2
fun IntRange.end() = endInclusive + 1
fun List<Sector>.boundaries(range: (Sector) -> IntRange): List<Int> = 
    flatMap { listOf(range(it).start, range(it).end()) }.sorted().distinct()
fun <T> List<T>.indexMap() = mapIndexed { index, value -> value to index }.toMap()

val sectors = instructions.map { it.sector }
val xs = sectors.boundaries { it.x }
val ys = sectors.boundaries { it.y }
val zs = sectors.boundaries { it.z }

val xIndexMap = xs.indexMap()
val yIndexMap = ys.indexMap()
val zIndexMap = zs.indexMap()

val bits = Array(xs.size) { Array(ys.size) { BooleanArray(zs.size) { false } } }
for (instruction in instructions) {
    val s = instruction.sector
    for (x in xIndexMap[s.x.start]!! until xIndexMap[s.x.end()]!!) {
        for (y in yIndexMap[s.y.start]!! until yIndexMap[s.y.end()]!!) {
            for (z in zIndexMap[s.z.start]!! until zIndexMap[s.z.end()]!!) {
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
