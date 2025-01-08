#!/usr/bin/env kotlin

enum class Operation { TOGGLE, ON, OFF }
data class Instruction(val op: Operation, val xRange: IntRange, val yRange: IntRange)

val pattern = Regex("""(turn on|turn off|toggle) (\d+),(\d+) through (\d+),(\d+)""")
fun parse(input: String): Instruction {
    val (opText, x1, y1, x2, y2) = pattern.find(input)!!.destructured
    val op = when (opText) {
        "turn on" -> Operation.ON
        "turn off" -> Operation.OFF
        "toggle" -> Operation.TOGGLE
        else -> null
    }
    return Instruction(op!!, x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt())
}

fun List<Instruction>.forEachPoint(block: (Operation, Int, Int) -> Unit) {
    for (instruction in instructions) {
        for (y in instruction.yRange) {
            for (x in instruction.xRange) {
                block(instruction.op, x, y)
            }
        }
    }
}

val lines = java.io.File(args[0]).readLines()
val instructions = lines.map { parse(it) }

val lights = Array(1000) { BooleanArray(1000) }
instructions.forEachPoint { op, x, y ->
    lights[y][x] = when (op) {
        Operation.ON -> true
        Operation.OFF -> false
        Operation.TOGGLE -> !lights[y][x]
    }
}
println(lights.sumOf { it.count { it } })

val brightness = Array(1000) { IntArray(1000) }
instructions.forEachPoint { op, x, y ->
    brightness[y][x] += when (op) {
        Operation.ON -> 1
        Operation.OFF -> if (brightness[y][x] == 0) 0 else -1
        Operation.TOGGLE -> 2
    }
}
println(brightness.sumOf { it.sum() })
