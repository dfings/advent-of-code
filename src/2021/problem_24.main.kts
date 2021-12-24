#!/usr/bin/env kotlin

data class State(val registers: MutableList<Long> = mutableListOf(0, 0, 0, 0))
enum class OpCode { INP, ADD, MUL, DIV, MOD, EQL }
sealed interface Instruction : (State) -> Unit

data class BinaryOpOnConstant(val op: OpCode, val register: Int, val constant: Long) : Instruction {
    override fun invoke(state: State) = when(op) {
        OpCode.ADD -> state.registers[register] += constant
        OpCode.MUL -> state.registers[register] *= constant
        OpCode.DIV -> state.registers[register] /= constant
        OpCode.MOD -> state.registers[register] %= constant
        OpCode.EQL -> state.registers[register] = if (state.registers[register] == constant) 1 else 0
        else -> error("")
    }
}

data class BinaryOpOnRegister(val op: OpCode, val register: Int, val input: Int) : Instruction {
    override fun invoke(state: State) = when(op) {
        OpCode.ADD -> state.registers[register] += state.registers[input]
        OpCode.MUL -> state.registers[register] *= state.registers[input]
        OpCode.DIV -> state.registers[register] /= state.registers[input]
        OpCode.MOD -> state.registers[register] %= state.registers[input]
        OpCode.EQL -> state.registers[register] = if (state.registers[register] == state.registers[input]) 1 else 0
        else -> error("")
    }
}

fun parseInput(input: List<String>): List<List<Instruction>> {
    val validRegisters = setOf("w", "x", "y", "z")
    fun String.toRegister() = this[0] - 'w'

    val chunkedInstructions = mutableListOf<MutableList<Instruction>>()
    var current = mutableListOf<Instruction>()
    input.forEach {
        val parts = it.split(" ")
        val op = OpCode.valueOf(parts[0].uppercase())
        if (op == OpCode.INP) {
            chunkedInstructions.add(current)
            current = mutableListOf<Instruction>()
        } else {
            if (parts[2] in validRegisters) {
                current.add(BinaryOpOnRegister(op, parts[1].toRegister(), parts[2].toRegister()))
            } else {
                current.add(BinaryOpOnConstant(op, parts[1].toRegister(), parts[2].toLong()))
            }
        }
    }
    chunkedInstructions.add(current)
    return chunkedInstructions.drop(1)
}

data class Vertex(val z: Long, val index: Int)
data class Path(val v: Vertex, val number: Long, val fScore: Long)

fun Path.successors(instructions: List<List<Instruction>>) = sequence {
    if (v.index < 14) {
        for (w in 1L..9L) {
            val state = State(mutableListOf(w, 0, 0, v.z))
            instructions[v.index].forEach { it(state) }
            val zOut = state.registers.last()
            val newNumber = number * 10 + w
            var fScore = newNumber
            repeat (13 - v.index) { fScore *= 10 }
            yield(Path(Vertex(zOut, v.index + 1), newNumber, fScore))
        }
    }
}

fun solve(instructions: List<List<Instruction>>, comparator: java.util.Comparator<Path>): Long {
    val frontier = java.util.PriorityQueue<Path>(comparator)
    frontier += Path(Vertex(0, 0), 0, 0)
    val seen = HashSet<Vertex>()
    while (!frontier.isEmpty()) {
        val path = frontier.poll()
        if (path.v in seen) continue
        if (path.v.index == 14 && path.v.z == 0L) return path.number
        seen += path.v
        path.successors(instructions).forEach { frontier += it }
    }
    error("No solution!")
}

val instructions = parseInput(java.io.File(args[0]).readLines())
println(solve(instructions) { 
    a, b -> b.fScore.compareTo(a.fScore) 
})

println(solve(instructions) { 
    a, b -> a.fScore.compareTo(b.fScore) 
})
