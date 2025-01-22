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
    fun Position.next(): Position {
        if (dir == '<' || dir == '>') {
            val line = lines[y]
            val xMin = line.indexOfFirst { it != ' '}
            val xMax = line.indexOfLast { it != ' '}
            var newX = x + if (dir == '>') 1 else -1
            if (newX > xMax) newX = xMin
            if (newX < xMin) newX = xMax
            return if (line[newX] == '#') this else copy(x = newX)
        } else {
            val yMin = lines.indexOfFirst { it.getOrElse(x) { ' ' } != ' '}
            val yMax = lines.indexOfLast { it.getOrElse(x) { ' ' } != ' '}
            var newY = y + if (dir == 'v') 1 else -1
            if (newY > yMax) newY = yMin
            if (newY < yMin) newY = yMax
            return if (lines[newY][x] == '#') this else copy(y = newY)
        }
    }

    fun walk(): Int {
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
