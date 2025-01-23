#!/usr/bin/env kotlin

val turnRight = "^>v<^".zipWithNext().toMap()
val turnLeft = "^<v>^".zipWithNext().toMap()

sealed interface Instruction
data class Move(val steps: Int): Instruction
data class Face(val dir: Char) : Instruction

val pattern = Regex("""(\d+)(L|R)?""")
fun parseInstructions(input: String): List<Instruction> {
    var dir = '>'
    val output = mutableListOf<Instruction>()
    for (match in pattern.findAll(input)) {
        val (distance, turn) = match.destructured
        output += Move(distance.toInt())
        dir = when (turn) {
            "R" -> turnRight.getValue(dir)
            "L" -> turnLeft.getValue(dir)
            else -> dir
        }
        output.add(Face(dir))
    }
    return output
}

data class Position(val x: Int, val y: Int, val dir: Char)
data class Map(val lines: List<String>) {
    val xMins = lines.map { it.indexOfFirst { it != ' ' } }
    val xMaxs = lines.map { it.indexOfLast { it != ' ' } }
    val xRange = xMins.min()..xMaxs.max()
    val yMins = xRange.map { x -> lines.indexOfFirst { it.getOrElse(x) { ' ' } != ' '} }
    val yMaxs = xRange.map { x -> lines.indexOfLast { it.getOrElse(x) { ' ' } != ' '} }

    fun Position.next(cube: Boolean = false): Position {
        val next = when (dir) {
            '<' -> when {
                x - 1 >= xMins[y] -> copy(x = x - 1)
                !cube -> copy(x = xMaxs[y])
                y in 0..49 -> TODO()
                y in 50..99 -> TODO()
                y in 100..149 -> TODO()
                else -> TODO()
            }
            '>' -> when {
                x + 1 <= xMaxs[y] -> copy(x = x + 1)
                !cube -> copy(x = xMins[y])
                y in 0..49 -> TODO()
                y in 50..99 -> TODO()
                y in 100..149 -> TODO()
                else -> TODO()
            }
            '^' -> when {
                y - 1 >= yMins[x] -> copy(y = y - 1)
                !cube -> copy(y = yMaxs[x])   
                x in 0..49 -> TODO()
                x in 50..99 -> TODO()
                else -> TODO()
            }
            else -> when {
                y + 1 <= yMaxs[x] -> copy(y = y + 1)
                !cube -> copy(y = yMins[x])
                x in 0..49 -> TODO()
                x in 50..99 -> TODO()
                else -> TODO()
            }
        }
        return if (lines[next.y][next.x] == '#') this else next
    }

    fun walk(cube: Boolean = false): Int {
        var p = Position(lines[0].indexOf('.'), 0, '>')
        for (instruction in instructions) {
            check(lines[p.y][p.x] == '.')
            when (instruction) {
                is Face -> p = p.copy(dir = instruction.dir)
                is Move -> {
                    repeat (instruction.steps) {
                        p = p.next(cube)
                        check(lines[p.y][p.x] == '.')
                    }
                }
            }
        }

        val row = p.y + 1
        val column = p.x + 1
        val facing = ">v<^".indexOf(p.dir)
        return 1000 * row + 4 * column + facing
    }
}

val input = java.io.File(args[0]).readLines()
val map = Map(input.dropLast(2))
val instructions = parseInstructions(input.last())
println(map.walk())
