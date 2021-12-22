#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

data class Point(val x: Int, val y: Int, val z: Int)
data class Sector(val x: IntRange, val y: IntRange, val z: IntRange) {
    init {
        check(x.start <= x.endInclusive)
        check(y.start <= y.endInclusive)
        check(z.start <= z.endInclusive)
    }

    val volume = (x.endInclusive - x.start + 1L) * (y.endInclusive - y.start + 1) * (z.endInclusive - z.start + 1)

    fun contains(p: Point) = p.x in x && p.y in y && p.z in z

    fun contains(o: Sector): Boolean = 
        (o.x.start in x && o.y.start in y && o.z.start in z) &&
        (o.x.endInclusive in x && o.y.endInclusive in y && o.z.endInclusive in z)

    fun intersects(o: Sector): Boolean =
        (x.start in o.x && y.start in o.y && z.start in o.z) ||
        (x.endInclusive in o.x && y.endInclusive in o.y && z.endInclusive in o.z) ||
        (o.x.start in x && o.y.start in y && o.z.start in z) ||
        (o.x.endInclusive in x && o.y.endInclusive in y && o.z.endInclusive in z)
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

fun toPoints(a: IntRange, b: IntRange) = sortedSetOf(a.start, a.endInclusive + 1, b.start, b.endInclusive + 1)
fun split(a: Sector, b: Sector) = buildList {
    for (xs in toPoints(a.x, b.x).windowed(2)) {
        for (ys in toPoints(a.y, b.y).windowed(2)) {
            for (zs in toPoints(a.z, b.z).windowed(2)) {
                add(Sector(xs[0]..xs[1]-1, ys[0]..ys[1]-1, zs[0]..zs[1]-1))
            }
        }
    }
}

val sectorComparator: java.util.Comparator<Sector> = java.util.Comparator.comparing<Sector, Long> { -it.volume } 
    .thenComparing { it: Sector -> it.x.start }
    .thenComparing { it: Sector -> it.x.endInclusive }
    .thenComparing { it: Sector -> it.y.start }
    .thenComparing { it: Sector -> it.y.endInclusive }
    .thenComparing { it: Sector -> it.z.start }
    .thenComparing { it: Sector -> it.z.endInclusive }

val sectors = java.util.TreeSet<Sector>(sectorComparator)
sectors.addAll(instructions.map { it.sector })
println(sectors.size)
while(true) {
    val toSplit = sectors.firstNotNullOfOrNull { a -> sectors.firstNotNullOfOrNull { b -> if (a !== b && a.intersects(b)) a to b else null } }
    if (toSplit == null) break
    println(toSplit)
    val splits = split(toSplit.first, toSplit.second)
    println(splits)
    sectors.addAll(splits)
    sectors.removeAll(toSplit.toList())
    println(sectors.size)
}
println(sectors.size)

val on = mutableSetOf<Sector>()
instructions.forEach { instruction ->
    val matching = sectors.filter { instruction.sector.contains(it) }
    if (instruction.on) {
        on.addAll(matching)
    } else {
        on.removeAll(matching)
    }
}
println(on.map { it.volume }.sum())
