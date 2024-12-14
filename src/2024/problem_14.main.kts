#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int) 
fun Point(x: String, y: String) = Point(x.toInt(), y.toInt())

data class Robot(val p: Point, val v: Point)
fun parseRobot(line: String): Robot {
    val pattern = Regex(List(4) { "(-?\\d+)" }.joinToString(".*?"))
    val (px, py, vx, vy) = pattern.find(line)!!.destructured
    return Robot(Point(px, py), Point(vx, vy))
}

fun List<Robot>.move(room: Point, n: Int) = map { 
    val newP = Point((it.p.x + it.v.x * n).mod(room.x), (it.p.y + it.v.y * n).mod(room.y))
    it.copy(p = newP)
}

fun List<Robot>.score(room: Point): Int {
    val xMid = room.x / 2
    val yMid = room.y / 2
    return count { it.p.x < xMid && it.p.y < yMid } *
           count { it.p.x < xMid && it.p.y > yMid } *
           count { it.p.x > xMid && it.p.y < yMid } *
           count { it.p.x > xMid && it.p.y > yMid }
}

fun List<Robot>.isTree() = map { it.p }.toSet().size == size

val lines = java.io.File(args[0]).readLines()
val robots = lines.map { parseRobot(it) }
val room = Point(101, 103)
println(robots.move(room, 100).score(room))

val robotSequence = generateSequence(robots to 0) { it.first.move(room, 1) to it.second + 1 }
val tree = robotSequence.first { it.first.isTree() }
println(tree.second)
