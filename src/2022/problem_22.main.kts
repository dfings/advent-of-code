#!/usr/bin/env kotlin

enum class Direction { EAST, SOUTH, WEST, NORTH }
val turnRight = (Direction.entries + Direction.EAST).zipWithNext().toMap()
val turnLeft = (listOf(Direction.EAST) + Direction.entries.reversed()).zipWithNext().toMap()

sealed interface Instruction
data class Move(val steps: Int): Instruction
data object TurnLeft : Instruction
data object TurnRight : Instruction

val pattern = Regex("""(\d+)(L|R)?""")
fun parseInstructions(input: String): List<Instruction> =
    pattern.findAll(input).toList().flatMap { match ->
        val (distance, turn) = match.destructured
        val move = Move(distance.toInt())
        when (turn) {
            "L" -> listOf(move, TurnLeft)
            "R" -> listOf(move, TurnRight)
            else -> listOf(move)
        }
    }

data class Position(val x: Int, val y: Int, val dir: Direction)
data class Map(val lines: List<String>) {
    val xMins = lines.map { it.indexOfFirst { it != ' ' } }
    val xMaxs = lines.map { it.indexOfLast { it != ' ' } }
    val xRange = xMins.min()..xMaxs.max()
    val yMins = xRange.map { x -> lines.indexOfFirst { it.getOrElse(x) { ' ' } != ' '} }
    val yMaxs = xRange.map { x -> lines.indexOfLast { it.getOrElse(x) { ' ' } != ' '} }

    fun Position.next(cube: Boolean = false): Position {
        val xf = x / 50
        val yf = y / 50
        val next = when (dir) {
            Direction.WEST -> when {
                x - 1 >= xMins[y] -> copy(x = x - 1)
                !cube -> copy(x = xMaxs[y])
                yf == 0 -> Position(x = 0, y = 149 - y, dir = Direction.EAST)
                yf == 1 -> Position(x = y - 50, y = 100, dir = Direction.SOUTH)
                yf == 2 -> Position(x = 50, y = 149 - y, dir = Direction.EAST)
                else -> Position(y - 100, y = 0, dir = Direction.SOUTH)
            }
            Direction.EAST -> when {
                x + 1 <= xMaxs[y] -> copy(x = x + 1)
                !cube -> copy(x = xMins[y])
                yf == 0 -> Position(x = 99, y = 149 - y, dir = Direction.WEST)
                yf == 1 -> Position(x = y + 50, y = 49, dir = Direction.NORTH)
                yf == 2 -> Position(x = 149, y = 149 - y, dir = Direction.WEST)
                else -> Position(x = y - 100, y = 149, dir = Direction.NORTH)
            }
            Direction.NORTH -> when {
                y - 1 >= yMins[x] -> copy(y = y - 1)
                !cube -> copy(y = yMaxs[x])   
                xf == 0 -> Position(x = 50, y = 50 + x, dir = Direction.EAST)
                xf == 1 -> Position(x = 0, y = 100 + x, dir = Direction.EAST)
                else -> Position(x = x - 100, y = 199, dir = Direction.NORTH)
            }
            Direction.SOUTH -> when {
                y + 1 <= yMaxs[x] -> copy(y = y + 1)
                !cube -> copy(y = yMins[x])
                xf == 0 -> Position(x = x + 100 , y = 0, dir = Direction.SOUTH)
                xf == 1 -> Position(x = 49, y = x + 100 , dir = Direction.WEST)
                else -> Position(x = 99, y = x - 50, dir = Direction.WEST)
            }
        }
        return if (lines[next.y][next.x] == '#') this else next
    }

    fun walk(instructions: List<Instruction>, cube: Boolean = false): Int {
        var p = Position(lines[0].indexOf('.'), 0, Direction.EAST)
        for (instruction in instructions) {
            when (instruction) {
                is TurnLeft -> p = p.copy(dir = turnLeft.getValue(p.dir))
                is TurnRight -> p = p.copy(dir = turnRight.getValue(p.dir))
                is Move -> {
                    for (i in 1..instruction.steps) {
                        val np = p.next(cube)
                        if (np == p) break
                        p = np
                    }
                }
            }
        }
        return 1000 * (p.y + 1) + 4 * (p.x + 1) + Direction.entries.indexOf(p.dir)
    }
}

fun solve(lines: List<String>) {
    val map = Map(lines.dropLast(2))
    val instructions = parseInstructions(lines.last())
    println(map.walk(instructions))
    println(map.walk(instructions, true))
}

solve(java.io.File(args[0]).readLines())
