#!/usr/bin/env kotlin

data class Point(val x: Double, val y: Double, val z: Double)
data class Velocity(val dx: Double, val dy: Double, val dz: Double)
data class Hail(val p: Point, val v: Velocity)
data class Segment(val p1: Point, val p2: Point)

fun String.parse(): Hail {
    val (first, second) = split(" @ ")
    val (x, y, z) = first.split(", ").map { it.trim().toDouble() }
    val (dx, dy, dz) = second.split(", ").map { it.trim().toDouble() }
    return Hail(Point(x, y, z), Velocity(dx, dy, dz))
}

fun Hail.xyIntersect(o: Hail): Point? {
    val dx = o.p.x - p.x
    val dy = o.p.y - p.y
    val det = o.v.dx * v.dy - o.v.dy * v.dx
    if (det == 0.0) return null
    val t1 = (dy * o.v.dx - dx * o.v.dy) / det
    val t2 = (dy * v.dx - dx * v.dy) / det
    if (t1 < 0 || t2 < 0) return null
    return Point(p.x + v.dx * t1, p.y + v.dy * t1, p.z + v.dz * t1)
}

fun Point.inBox(xyMin: Double, xyMax: Double): Boolean =
    x >= xyMin && y >= xyMin && x <= xyMax && y <= xyMax

fun <T> Iterable<T>.allPairs() =
    this.flatMapIndexed { i, a -> this.mapIndexedNotNull() { j, b -> if (i < j) a to b else null } }

fun solve(lines: List<String>) {
    val hail = lines.map { it.parse() }
    val hailPairs = hail.allPairs()
    println(hailPairs.mapNotNull { (a, b) -> a.xyIntersect(b) }.count { it.inBox(200000000000000.0, 400000000000000.0) })
}

solve(java.io.File(args[0]).readLines())
