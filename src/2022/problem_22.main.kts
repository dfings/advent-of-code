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

typealias Map = List<String>
data class Position(val x: Int, val y: Int, val dir: Char)

fun Map.next(p: Position): Position {
    if (p.dir == '<' || p.dir == '>') {
        val line = get(p.y)
        val xMin = line.indexOfFirst { it != ' '}
        val xMax = line.indexOfLast { it != ' '}
        var newX = p.x + if (p.dir == '>') 1 else -1
        if (newX > xMax) newX = xMin
        if (newX < xMin) newX = xMax
        return if (line[newX] == '#') p else p.copy(x = newX)
    } else {
        val yMin = indexOfFirst { it.getOrElse(p.x) { ' ' } != ' '}
        val yMax = indexOfLast { it.getOrElse(p.x) { ' ' } != ' '}
        var newY = p.y + if (p.dir == 'v') 1 else -1
        if (newY > yMax) newY = yMin
        if (newY < yMin) newY = yMax
        return if (get(newY).getOrNull(p.x) == '#') p else p.copy(y = newY)
    }
}

fun Map.walk(): Int {
    var p = Position(get(0).indexOf('.'), 0, '>')
    for (instruction in instructions) {
        check(this[p.y][p.x] == '.')
        when (instruction) {
            is Face -> p = p.copy(dir = instruction.dir)
            is Move -> {
                repeat (instruction.steps) {
                    p = next(p)
                    check(this[p.y][p.x] == '.')
                }
            }
        }
    }

    val row = p.y + 1
    val column = p.x + 1
    val facing = ">v<^".indexOf(p.dir)
    return 1000 * row + 4 * column + facing
}

val lines = java.io.File(args[0]).readLines()
val map = lines.dropLast(2)
val instructions = parseInstructions(lines.last())
println(map.walk())
