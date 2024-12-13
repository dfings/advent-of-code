#!/usr/bin/env kotlin

data class Point(val x: Long, val y: Long) 
fun Point(x: String, y: String) = Point(x.toLong(), y.toLong())
operator fun Point.plus(p: Point) = Point(x + p.x, y + p.y)
operator fun Point.times(i: Long) = Point(x * i, y * i)

data class Machine(val a: Point, val b: Point, val prize: Point)

fun parseMachine(lines: List<String>): Machine {
    val pattern = Regex("""(\d+).*?(\d+).*?(\d+).*?(\d+).*?(\d+).*?(\d+)""")
    val (ax, ay, bx, by, px, py) = pattern.find(lines.joinToString())!!.destructured
    return Machine(Point(ax, ay), Point(bx, by), Point(px, py))
}

fun Machine.bestPrice(): Long {
    val db = (a.x * prize.y - a.y * prize.x) / (a.x * b.y - a.y * b.x)
    val da = (prize.y - b.y * db) / a.y
    return if (a * da + b * db == prize) da * 3 + db else 0
}

val lines = java.io.File(args[0]).readLines()
val machines = lines.chunked(4).map { parseMachine(it) }
val extra = Point(10000000000000L, 10000000000000L)
val machines2 = machines.map { it.copy(prize = it.prize + extra )}

println(machines.sumOf { it.bestPrice() })
println(machines2.sumOf { it.bestPrice() })
