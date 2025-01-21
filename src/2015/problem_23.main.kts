#!/usr/bin/env kotlin

data class Instruction(val code: String, val register: Int, val offset: Int)

fun parse(input: String): Instruction {
    val code = input.take(3)
    return when (code) {
        "hlf", "tpl", "inc" -> Instruction(code, input[4] - 'a', -1)
        "jmp" -> Instruction(code, -1, input.drop(4).toInt())
        else -> Instruction(code, input[4] - 'a', input.drop(7).toInt())
    }
}

fun executeProgram(instructions: List<Instruction>, initialState: List<Long>): Long {
    var instructionPointer = 0
    val registers = initialState.toLongArray()
    while (instructionPointer < instructions.size) {
        val i = instructions[instructionPointer]
        var jumped = false
        fun jump(offset: Int) {
            instructionPointer += offset
            jumped = true
        }
        when (i.code) {
            "hlf" -> registers[i.register] /= 2L
            "tpl" -> registers[i.register] *= 3L
            "inc" -> registers[i.register]++
            "jmp" -> jump(i.offset)
            "jie" -> if (registers[i.register] % 2L == 0L) jump(i.offset)
            "jio" -> if (registers[i.register] == 1L) jump(i.offset)
        }
        if (!jumped) instructionPointer++
    }
    return registers[1]
}

val lines = java.io.File(args[0]).readLines()
val instructions = lines.map { parse(it) }
println(executeProgram(instructions, listOf(0L, 0L)))
println(executeProgram(instructions, listOf(1L, 0L)))
