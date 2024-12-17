#!/usr/bin/env kotlin

data class Program(
    var a: Long, 
    var b: Long, 
    var c: Long,
    val instructions: List<Int>, 
    var instructionPointer: Int = 0,
    val output: MutableList<Int> = mutableListOf<Int>(),
) {
    fun combo(value: Int) = when (value) {
        in 0..3 -> value.toLong()
        4 -> a
        5 -> b
        6 -> c
        else -> throw IllegalArgumentException()
    }

    fun execute(instruction: Int, operand: Int) {
        when (instruction) {
            0 -> a = a shr combo(operand).toInt()
            1 -> b = b xor operand.toLong()
            2 -> b = combo(operand).mod(8L)
            3 -> if (a != 0L) instructionPointer = operand - 2
            4 -> b = b xor c
            5 -> output += combo(operand).mod(8L).toInt()
            6 -> b = a shr combo(operand).toInt()
            7 -> c = a shr combo(operand).toInt()
        }
    }

    fun step() {
        execute(instructions[instructionPointer], instructions[instructionPointer + 1])
        instructionPointer += 2
    }

    fun execute() {
        while (instructionPointer < instructions.size) {
            step()
        }
    }
}

fun parseProgram(lines: List<String>): Program {
    val pattern = Regex("""(\d+).*?(\d+).*?(\d+).*?([\d,]+)""")
    val (a, b, c, i) = pattern.find(lines.joinToString(""))!!.destructured
    return Program(a.toLong(), b.toLong(), c.toLong(), i.split(",").map { it.toInt() })
}

val lines = java.io.File(args[0]).readLines()
val program = parseProgram(lines)

program.execute()
println(program.output.joinToString(","))

fun executeCorruptedProgram(a: Long): List<Int> {
    val corruptedProgram = Program(a, 0, 0, program.instructions)
    corruptedProgram.execute()
    return corruptedProgram.output
}

fun buildInput(target: List<Int>): Long {
    var a = if (target.size == 1) 0 else buildInput(target.drop(1)) shl 3
    while (executeCorruptedProgram(a) != target) {
        a++
    }
    return a
}
println(buildInput(program.instructions))
