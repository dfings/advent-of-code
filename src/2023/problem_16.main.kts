#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int)
enum class Direction(val dx: Int, val dy: Int) { NORTH(0, -1),  EAST(1, 0), SOUTH(0, 1), WEST(-1, 0) }
operator fun Point.plus(d: Direction) = Point(x + d.dx, y + d.dy)

data class Beam(val p: Point, val d: Direction)

fun Beam.reflectForward() = copy(d = when(d) {
    Direction.NORTH -> Direction.EAST
    Direction.EAST -> Direction.NORTH
    Direction.SOUTH -> Direction.WEST
    Direction.WEST -> Direction.SOUTH
})

fun Beam.reflectBackward() = copy(d = when(d) {
    Direction.NORTH -> Direction.WEST
    Direction.WEST -> Direction.NORTH
    Direction.SOUTH -> Direction.EAST
    Direction.EAST -> Direction.SOUTH
})

fun simulate(lines: List<String>, start: Beam): Int {
    val seen = mutableSetOf<Beam>()
    val active = ArrayDeque(listOf(start))
    while (active.isNotEmpty()) {
        val beam = active.removeFirst()
        if (!seen.add(beam)) continue
        val next = beam.copy(p = beam.p + beam.d)
        if (next.p.x !in lines[0].indices || next.p.y !in lines.indices) continue
        when (lines[next.p.y][next.p.x]) {
            '.' -> active += next
            '/' -> active += next.reflectForward()
            '\\' -> active += next.reflectBackward()
            '|' -> when (beam.d) {
                Direction.NORTH, Direction.SOUTH -> active += next
                Direction.EAST, Direction.WEST ->  {
                    active += next.reflectForward()
                    active += next.reflectBackward()
                }
            }
            '-' -> when (beam.d) {
                Direction.NORTH, Direction.SOUTH -> {
                    active += next.reflectForward()
                    active += next.reflectBackward()
                }
                Direction.EAST, Direction.WEST -> active += next
            }
        }
    }
    return seen.map { it.p }.toSet().size - 1
}

fun solve(lines: List<String>) {
    println(simulate(lines, (Beam(Point(-1, 0), Direction.EAST))))
    println(lines.indices.maxOf { v ->
        Direction.entries.maxOf { d ->
            when (d) {
                Direction.NORTH -> simulate(lines, Beam(Point(v, lines.size), d))
                Direction.SOUTH -> simulate(lines, Beam(Point(v, -1), d))
                Direction.WEST -> simulate(lines, Beam(Point(lines.size, v), d))
                Direction.EAST -> simulate(lines, Beam(Point(-1, v), d))
            }
        }
    })
}

solve(java.io.File(args[0]).readLines())
