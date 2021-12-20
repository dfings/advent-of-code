#!/usr/bin/env kotlin

import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int) {
    operator fun minus(o: Point): Point = Point(x - o.x, y - o.y, z - o.z)
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
    // X -> X
    Transform(X, Y, Z), Transform(X, Z, Y), Transform(X, NY, NZ), Transform(X, NZ, NY),
    Transform(NX, NY, Z), Transform(NX, Z, NY), Transform(NX, Y, NZ), Transform(NX, NZ, NY),
    // X -> Y
    Transform(Y, NX, Z), Transform(Y, Z, NX), Transform(Y, X, NZ), Transform(Y, NZ, X),
    Transform(NY, X, Z), Transform(NY, Z, X), Transform(NY, NX, NZ), Transform(NY, NZ, NX),
    // X -> Z
    Transform(Z, X, Y), Transform(Z, Y, X), Transform(Z, NX, NY), Transform(Z, NY, NX),
    Transform(NZ, NX, Y), Transform(NZ, Y, NX), Transform(NZ, X, NY), Transform(NZ, NY, X),
)
/*

val transforms = listOf(
    // X -> X
    Transform(X, Y, Z), Transform(X, Z, Y), Transform(X, NY, NZ), Transform(X, NZ, NY),
    Transform(NX, NY, Z), Transform(NX, Z, NY), Transform(NX, Y, NZ), Transform(NX, NZ, NY),
    // X -> Y
    Transform(NY, X, Z), Transform(Y, X, NZ), Transform(Z, X, NY), Transform(NZ, X, Y),
    Transform(Y, NX, Z), Transform(Z, NX, Y), Transform(NY, NX, NZ), Transform(NZ, NX, NY),
    // X -> Z
    Transform(Y, Z, X), Transform(Z, Y, X), Transform(NY, NZ, X), Transform(NZ, NY, X),
    Transform(NY, Z, NX), Transform(Z, NY, NX), Transform(Y, NZ, NX), Transform(NZ, Y, X),
)
 */

data class Scanner(val index: Int, val beacons: List<Point>) {
    val signatures: Map<Set<Int>, Pair<Point, Point>> =
        beacons.flatMap { a -> beacons.mapNotNull { b ->
            if (a === b) null else a.offsetSignature(b) to (a to b)
        }}.toMap()

    fun commonSignatures(o: Scanner) = (signatures.keys intersect o.signatures.keys)
    // 66 = 12 * (12 - 1) / 2
    fun overlaps(o: Scanner) = commonSignatures(o).size >= 66

    fun normalize(transform: Transform, offset: Point) =
        Scanner(index, beacons.map { transform(it) - offset })
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
    .filter { (a, b) -> a.index != b.index && a.overlaps(b) }
    .map { (a, b) -> a.index to b.index }
    .groupBy { it.first }
    .mapValues { it.value.map { it.second } }

println(overlaps)

val scanner0 = scanners[0]
val scanner2 = scanners[2]
val commonSignatures = scanner0.commonSignatures(scanner2)
val pair1 = scanner0.signatures.getValue(commonSignatures.first())
val pair2 = scanner2.signatures.getValue(commonSignatures.first())
val transform = transforms.single { 
    pair1.first - pair1.second == it(pair2.first) - it(pair2.second) ||
    pair1.first - pair1.second == it(pair2.second) - it(pair2.first)
}
val scannerOffset =
    if (pair1.first - pair1.second == transform(pair2.first) - transform(pair2.second)) {
        check(transform(pair2.first) - pair1.first == transform(pair2.second) - pair1.second)
        transform(pair2.first) - pair1.first
    } else {
        check(transform(pair2.second) - pair1.first == transform(pair2.first) - pair1.second)
        transform(pair2.second) - pair1.first
    }
println(scannerOffset)
val nscanner2 = scanner2.normalize(transform, scannerOffset)
println((scanner0.beacons.toSet() intersect nscanner2.beacons.toSet()).size)
