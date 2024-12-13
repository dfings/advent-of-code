#!/usr/bin/env kotlin

import kotlin.math.min

data class Point(val x: Long, val y: Long) 
fun Point(x: String, y: String) = Point(x.toLong(), y.toLong())
operator fun Point.plus(p: Point) = Point(x + p.x, y + p.y)
operator fun Point.times(i: Long) = Point(x * i, y * i)

data class Play(val aMoves: Long, val bMoves: Long, val price: Long)
data class Machine(val a: Point, val b: Point, val prize: Point)

tailrec fun gcd(i: Long, j: Long): Long = when {
  j == 0L -> i
  else -> gcd(j, i % j)
}
fun lcm(x: Long, y: Long): Long = (x * y) / gcd(x, y)

fun Machine.bestPlay(): Play? {
    val zx = lcm(a.x, a.y) / a.x
    val zy = lcm(a.x, a.y) / a.y

    val bm = (zx * prize.x - zy * prize.y) / (zx * b.x - zy * b.y)
    val am = (prize.x - b.x * bm) / a.x

    if ((a * am) + (b * bm) != prize) return null
    return Play(am, bm, am * 3 + bm)
}

fun parseMachine(lines: List<String>): Machine {
    val buttonPattern = Regex("""X\+(\d+), Y\+(\d+)""")
    val prizePattern =  Regex("""Prize: X=(\d+), Y=(\d+)""")
    val (ax, ay) = buttonPattern.find(lines[0])!!.destructured
    val (bx, by) = buttonPattern.find(lines[1])!!.destructured
    val (px, py) = prizePattern.find(lines[2])!!.destructured
    return Machine(Point(ax, ay), Point(bx, by), Point(px, py))
}

val lines = java.io.File(args[0]).readLines()
val machines = lines.chunked(4).map { parseMachine(it) }
println(machines.mapNotNull { it.bestPlay() }.sumOf { it.price })

val extra = Point(10000000000000L, 10000000000000L)
val machines2 = machines.map { it.copy(prize = it.prize + extra )}
println(machines2.mapNotNull { it.bestPlay() }.sumOf { it.price })
