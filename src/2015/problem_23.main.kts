#!/usr/bin/env kotlin

sealed interface Instruction
data class Half(val register: Int): Instruction
data class Triple(val register: Int): Instruction
data class Increment(val register: Int): Instruction
data class Jump(val offset: Int): Instruction
data class JumpIfEven(val register: Int, val offset: Int): Instruction
data class JumpIfOne(val register: Int, val offset: Int): Instruction

fun Char.register() = this - 'a'
fun parse(input: String): Instruction = when(input.take(3)) {
    "hlf" -> Half(input[4].register())
    "tpl" -> Triple(input[4].register())
    "inc" -> Increment(input[4].register())
    "jmp" -> Jump(input.drop(4).toInt())
    "jie" -> JumpIfEven(input[4].register(), input.drop(7).toInt())
    "jio" -> JumpIfOne(input[4].register(), input.drop(7).toInt())
    else -> throw IllegalArgumentException(input)
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
        when (i) {
            is Half -> registers[i.register] /= 2L
            is Triple -> registers[i.register] *= 3L
            is Increment -> registers[i.register]++
            is Jump -> jump(i.offset)
            is JumpIfEven -> if (registers[i.register] % 2L == 0L) jump(i.offset)
            is JumpIfOne -> if (registers[i.register] == 1L) jump(i.offset)
        }
        if (!jumped) instructionPointer++
    }
    return registers[1]
}

val lines = java.io.File(args[0]).readLines()
val instructions = lines.map { parse(it) }
println(executeProgram(instructions, listOf(0L, 0L)))
println(executeProgram(instructions, listOf(1L, 0L)))
