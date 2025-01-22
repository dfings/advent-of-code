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

fun Map.nextX(x: Int, y: Int, dir: Char): Int {
    val line = get(y)
    val xMin = line.indexOfFirst { it != ' '}
    val xMax = line.indexOfLast { it != ' '}
    var newX = x + if (dir == '>') 1 else -1
    if (newX > xMax) newX = xMin
    if (newX < xMin) newX = xMax
    return if (line[newX] == '#') x else newX
}

fun Map.nextY(x: Int, y: Int, dir: Char): Int {
    val yMin = indexOfFirst { it.getOrElse(x) { ' ' } != ' '}
    val yMax = indexOfLast { it.getOrElse(x) { ' ' } != ' '}
    var newY = y + if (dir == 'v') 1 else -1
    if (newY > yMax) newY = yMin
    if (newY < yMin) newY = yMax
    return if (get(newY).getOrNull(x) == '#') y else newY
}

fun Map.walk(): Int {
    var x = get(0).indexOf('.')
    var y = 0
    var dir = '>'
    for (instruction in instructions) {
        check(this[y][x] == '.')
        when (instruction) {
            is Face -> dir = instruction.dir
            is Move -> {
                repeat (instruction.steps) {
                    when (dir) {
                        '>', '<' -> x = nextX(x, y, dir)
                        '^', 'v' -> y = nextY(x, y, dir)
                    }
                    check(this[y][x] == '.')
                }
            }
        }
    }

    val row = y + 1
    val column = x + 1
    val facing = ">v<^".indexOf(dir)
    return 1000 * row + 4 * column + facing
}

val lines = java.io.File(args[0]).readLines()
val map = lines.dropLast(2)
val instructions = parseInstructions(lines.last())
println(map.walk())
