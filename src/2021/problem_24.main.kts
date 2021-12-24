#!/usr/bin/env kotlin

enum class OpCode { INP, ADD, MUL, DIV, MOD, EQL }
typealias Instruction = (LongArray) -> Unit

data class BinaryOpOnConstant(val op: OpCode, val register: Int, val constant: Long) : Instruction {
    override fun invoke(registers: LongArray) = when(op) {
        OpCode.ADD -> registers[register] += constant
        OpCode.MUL -> registers[register] *= constant
        OpCode.DIV -> registers[register] /= constant
        OpCode.MOD -> registers[register] %= constant
        OpCode.EQL -> registers[register] = if (registers[register] == constant) 1 else 0
        else -> error("")
    }
}

data class BinaryOpOnRegister(val op: OpCode, val register: Int, val input: Int) : Instruction {
    override fun invoke(registers: LongArray) = when(op) {
        OpCode.ADD -> registers[register] += registers[input]
        OpCode.MUL -> registers[register] *= registers[input]
        OpCode.DIV -> registers[register] /= registers[input]
        OpCode.MOD -> registers[register] %= registers[input]
        OpCode.EQL -> registers[register] = if (registers[register] == registers[input]) 1 else 0
        else -> error("")
    }
}

fun parseInput(input: List<String>): List<List<Instruction>> {
    val validRegisters = setOf("w", "x", "y", "z")
    fun String.toRegister() = this[0] - 'w'

    lateinit var current: ArrayList<Instruction>
    val perInputInstructions = ArrayList<List<Instruction>>()
    input.forEach {
        val parts = it.split(" ")
        val op = OpCode.valueOf(parts[0].uppercase())
        val register = parts[1].toRegister()
        when {
            op == OpCode.INP -> ArrayList<Instruction>().let {
                current = it
                perInputInstructions += it
            }
            parts[2] in validRegisters -> current += BinaryOpOnRegister(op, register, parts[2].toRegister())
            else -> current += BinaryOpOnConstant(op, register, parts[2].toLong())
        }
    }
    return perInputInstructions
}

val instructions = parseInput(java.io.File(args[0]).readLines())
fun HashMap<Pair<Long, Int>, Long>.solve(
    z: Long, 
    index: Int, 
    number: Long, 
    progression: LongProgression
): Long = getOrPut(z to index) {
    if (index == 14) return if (z == 0L) number else -1
    for (w in progression) {
        val registers = longArrayOf(w, 0, 0, z)
        instructions[index].forEach { it(registers) }
        val newNumber = number * 10 + w        
        val solution = solve(registers[3], index + 1, newNumber, progression)
        if (solution != -1L) return@getOrPut solution
    }
    return@getOrPut -1L
}

var start = System.nanoTime()
println(HashMap<Pair<Long, Int>, Long>().solve(0L, 0, 0L, 9L downTo 1L))
println("Runtime: ${(System.nanoTime() - start)/1_000_000}ms")

start = System.nanoTime()
println(HashMap<Pair<Long, Int>, Long>().solve(0L, 0, 0L, 1L..9L))
println("Runtime: ${(System.nanoTime() - start)/1_000_000}ms")
