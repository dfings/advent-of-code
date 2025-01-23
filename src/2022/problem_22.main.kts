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

    fun Position.next(): Position {
        if (dir == '<' || dir == '>') {
            var newX = x + if (dir == '>') 1 else -1
            val newP = when {
                newX < xMins[y] -> copy(x = xMaxs[y])
                newX > xMaxs[y] -> copy(x = xMins[y])
                else -> copy(x = newX)
            }
            return if (lines[newP.y][newP.x] == '#') this else newP
        } else {
            var newY = y + if (dir == 'v') 1 else -1
            val newP = when {
                newY < yMins[x] -> copy(y = yMaxs[x])
                newY > yMaxs[x] -> copy(y = yMins[x])
                else -> copy(y = newY)
            }
            return if (lines[newP.y][newP.x] == '#') this else newP
        }
    }

    fun walk(cube: Boolean = false): Int {
        var p = Position(lines[0].indexOf('.'), 0, '>')
        for (instruction in instructions) {
            check(lines[p.y][p.x] == '.')
            when (instruction) {
                is Face -> p = p.copy(dir = instruction.dir)
                is Move -> {
                    repeat (instruction.steps) {
                        p = p.next()
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
