#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int) {
    fun offsetSignature(o: Point) = setOf(abs(x - o.x), abs(y - o.y), abs(z - o.z))
}

data class Scanner(val index: Int, val beacons: List<Point>) {
    val signatures = beacons.flatMap { a -> beacons.mapNotNull { b ->
        if (a === b) null else a.offsetSignature(b) to (a to b)
    }}.toMap()

    // 66 = 12 * (12 - 1) / 2
    fun overlaps(o: Scanner) = (signatures.keys intersect o.signatures.keys).size >= 66
}

fun Iterator<String>.parseScanner(): Scanner {
    val regex = kotlin.text.Regex("--- scanner (\\d+) ---")
    val header = next()
    val (index) = checkNotNull(regex.find(header)).destructured
    val beacons = mutableListOf<Point>()
    while (hasNext()) {
        val line = next()
        if (line.isEmpty()) {
            break
        }
        val (x, y, z) = line.split(",")
        beacons += Point(x.toInt(), y.toInt(), z.toInt())
    }
    return Scanner(index.toInt(), beacons)
}

val lines = java.io.File(args[0]).readLines()
val scanners = mutableListOf<Scanner>()
lines.iterator().let { 
    while (it.hasNext()) {
        scanners.add(it.parseScanner())
    }
}

val overlaps = scanners.flatMap { a -> scanners.map { b -> a to b } }
    .filter { (a, b) -> a.index < b.index && a.overlaps(b) }
    .map { (a, b) -> a.index to b.index }
assert(overlaps.flatMap { it.toList() }.toSet().size == scanners.size)
println(overlaps.joinToString("\n"))
