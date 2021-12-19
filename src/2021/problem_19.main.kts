#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int) {
    fun offset(o: Point): Point = Point(x - o.x, y - o.y, z - o.z)
    fun offsetSignature(o: Point): Set<Int> = setOf(abs(x - o.x), abs(y - o.y), abs(z - o.z))
}

typealias Extractor = (Point) -> Int
val X: Extractor = { it.x }
val NX: Extractor = { -it.x }
val Y: Extractor = { it.y }
val NY: Extractor = { -it.y }
val Z: Extractor = { it.z }
val NZ: Extractor = { -it.z }

class Transform(val x: Extractor, val y: Extractor, val z: Extractor): (Point) -> Point {
    override operator fun invoke(p: Point) = Point(x(p), y(p), z(p))
}

val transforms = listOf(
    Transform(X, Y, Z), Transform(X, Z, Y), Transform(X, NY, NZ), Transform(X, NZ, NY),
    Transform(NX, Y, Z), Transform(NX, Z, Y), Transform(NX, NY, NZ), Transform(NX, NZ, NY),
    Transform(Y, X, Z), Transform(Y, Z, X), Transform(Y, NX, NZ), Transform(Y, NZ, NX),
    Transform(NY, X, Z), Transform(NY, Z, X), Transform(NY, NX, NZ), Transform(NY, NZ, NX),
    Transform(Z, Y, X), Transform(Z, X, Y), Transform(Z, NY, NX), Transform(Z, NX, NY),
    Transform(NZ, Y, X), Transform(NZ, X, Y), Transform(NZ, NY, NX), Transform(NZ, NX, NY),
)

data class Scanner(val index: Int, val beacons: List<Point>) {
    val signatures: Map<Set<Int>, Pair<Point, Point>> =
        beacons.flatMap { a -> beacons.mapNotNull { b ->
            if (a === b) null else a.offsetSignature(b) to (a to b)
        }}.toMap()

    fun commonSignatures(o: Scanner) = (signatures.keys intersect o.signatures.keys)
    // 66 = 12 * (12 - 1) / 2
    fun overlaps(o: Scanner) = commonSignatures(o).size >= 66
}

fun Iterator<String>.parseScanner(): Scanner {
    val regex = kotlin.text.Regex("--- scanner (\\d+) ---")
    val index = regex.find(next())!!.groupValues[1].toInt()
    val beacons = buildList {
        while (hasNext()) {
            val line = next()
            if (line.isEmpty()) {
                break
            }
            val (x, y, z) = line.split(",").map { it.toInt() }
            add(Point(x, y, z))
        }
    }
    return Scanner(index, beacons)
}

val lines = java.io.File(args[0]).readLines()
val scanners = buildList {
    val iter = lines.iterator()
    while (iter.hasNext()) {
        add(iter.parseScanner())
    }
}

val overlaps = scanners.flatMap { a -> scanners.map { b -> a to b } }
    .filter { (a, b) -> a.index < b.index && a.overlaps(b) }
    .map { (a, b) -> a.index to b.index }
assert(overlaps.flatMap { it.toList() }.toSet().size == scanners.size)
println(overlaps.joinToString("\n"))

val scanner0 = scanners[0]
val scanner2 = scanners[2]
val commonSignatures = scanner0.commonSignatures(scanner2)
val pair1 = scanner0.signatures.getValue(commonSignatures.first())
val pair2 = scanner2.signatures.getValue(commonSignatures.first())
val potentialTransforms = transforms.filter { 
    pair1.first.offset(pair1.second) == it(pair2.first).offset(it(pair2.second)) ||
    pair1.first.offset(pair1.second) == it(pair2.second).offset(it(pair2.first))
}
println(potentialTransforms.size)
