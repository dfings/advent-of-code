#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int) 
fun Point(x: String, y: String) = Point(x.toInt(), y.toInt())
operator fun Point.plus(p: Point) = Point(x + p.x, y + p.y)

data class Robot(val p: Point, val v: Point)
fun parseRobot(line: String): Robot {
    val pattern = Regex(List(4) { "(-?\\d+)" }.joinToString(".*?"))
    val (px, py, vx, vy) = pattern.find(line)!!.destructured
    return Robot(Point(px, py), Point(vx, vy))
}

class Bathroom(val xSize: Int, val ySize: Int) {
    fun move(robots: List<Robot>, times: Int) = robots.map {
        it.copy(
            p = Point((it.p.x + it.v.x * times).mod(xSize),
                      (it.p.y + it.v.y * times).mod(ySize))
        )
    }

    fun score(robots: List<Robot>): Int {
        val xMid = xSize / 2
        val yMid = ySize / 2
        return robots.count { it.p.x < xMid && it.p.y < yMid } *
               robots.count { it.p.x < xMid && it.p.y > yMid } *
               robots.count { it.p.x > xMid && it.p.y < yMid } *
               robots.count { it.p.x > xMid && it.p.y > yMid }
    }
}

fun isTree(robots: List<Robot>) = robots.map { it.p }.toSet().size == robots.size

val lines = java.io.File(args[0]).readLines()
val robots = lines.map { parseRobot(it) }
val room = Bathroom(101, 103)
println(room.score(room.move(robots, 100)))

val robotSequence = generateSequence(robots to 0) { room.move(it.first, 1) to it.second + 1 }
val tree = robotSequence.first { isTree(it.first)}
println(tree.second)
