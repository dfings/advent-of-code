#!/usr/bin/env kotlin

val turnRight = "^>v<^".zipWithNext().toMap()
val turnLeft = "^<v>^".zipWithNext().toMap()

sealed interface Instruction
data class Move(val steps: Int): Instruction
data class Turn(val dir: Char) : Instruction

val pattern = Regex("""(\d+)(L|R)?""")
fun parseInstructions(input: String): List<Instruction> {
    var dir = '>'
    val output = mutableListOf<Instruction>()
    for (match in pattern.findAll(input)) {
        val (distance, turn) = match.destructured
        output += Move(distance.toInt())
        if (!turn.isEmpty()) output += Turn(turn[0])
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
        val xf = x / 50
        val yf = y / 50
        val next = when (dir) {
            '<' -> when {
                x - 1 >= xMins[y] -> copy(x = x - 1)
                !cube -> copy(x = xMaxs[y])
                yf == 0 -> copy(x = 0, y = 149 - y, dir = '>')
                yf == 1 -> copy(x = y - 50, y = 100, dir = 'v')
                yf == 2 -> copy(x = 50, y = 149 - y, dir = '>')
                else -> copy(y - 100, y = 0, dir = 'v')
            }
            '>' -> when {
                x + 1 <= xMaxs[y] -> copy(x = x + 1)
                !cube -> copy(x = xMins[y])
                yf == 0 -> copy(x = 99, y = 149 - y, dir = '<')
                yf == 1 -> copy(x = y + 50, y = 49, dir = '^')
                yf == 2 -> copy(x = 149, y = 149 - y, dir = '<')
                else -> copy(x = y - 100, y = 149, dir = '^')
            }
            '^' -> when {
                y - 1 >= yMins[x] -> copy(y = y - 1)
                !cube -> copy(y = yMaxs[x])   
                xf == 0 -> copy(x = 50, y = 50 + x, dir = '>')
                xf == 1 -> copy(x = 0, y = 100 + x, dir = '>')
                else -> copy(x = x - 100, y = 199, dir = '^')
            }
            else -> when {
                y + 1 <= yMaxs[x] -> copy(y = y + 1)
                !cube -> copy(y = yMins[x])
                xf == 0 -> copy(x = x + 100 , y = 0, dir = 'v')
                xf == 1 -> copy(x = 49, y = x + 100 , dir = '<')
                else -> copy(x = 99, y = x - 50, dir = '<')
            }
        }
        return if (lines[next.y][next.x] == '#') this else next
    }

    fun walk(instructions: List<Instruction>, cube: Boolean = false): Int {
        var p = Position(lines[0].indexOf('.'), 0, '>')
        for (instruction in instructions) {
            check(lines[p.y][p.x] == '.')
            when (instruction) {
                is Turn -> when(instruction.dir) {
                    'L' -> p = p.copy(dir = turnLeft.getValue(p.dir))
                    'R' -> p = p.copy(dir = turnRight.getValue(p.dir))
                }
                is Move -> {
                    for (i in 1..instruction.steps) {
                        val np = p.next(cube)
                        if (np == p) break
                        p = np
                        check(lines[p.y][p.x] == '.')
                    }
                }
            }
        }
        val facing = ">v<^".indexOf(p.dir)
        return 1000 * (p.y + 1) + 4 * (p.x + 1) + facing
    }
}

fun solve(lines: List<String>) {
    val map = Map(lines.dropLast(2))
    val instructions = parseInstructions(lines.last())
    println(map.walk(instructions))
    println(map.walk(instructions, true))
}

solve(java.io.File(args[0]).readLines())
