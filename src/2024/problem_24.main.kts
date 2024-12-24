#!/usr/bin/env kotlin

sealed interface Gate {
    val value: Boolean
}

data class InputGate(override val value: Boolean) : Gate

data class AndGate(val a: Gate, val b: Gate) : Gate {
    override val value:Boolean
        get() = a.value && b.value
}

data class OrGate(val a: Gate, val b: Gate) : Gate {
    override val value:Boolean
        get() = a.value || b.value
}

data class XorGate(val a: Gate, val b: Gate) : Gate {
    override val value:Boolean
        get() = a.value xor b.value
}

val lines = java.io.File(args[0]).readLines()
val gates = mutableMapOf<String, Gate>()

for (line in lines.takeWhile { it != "" }) {
    val (name, value) = line.split(": ")
    gates[name] = InputGate(value == "1")
}

val logicPattern = Regex("""(\w+) (\w+) (\w+) ->""")
val logic = lines.dropWhile { it != "" }.drop(1).map { it.takeLast(3) to it }.toMap()
fun addLogicGate(name: String, definition: String): Gate {
    gates[name]?.let { return it }
    val (a, op, b) = logicPattern.find(definition)!!.destructured
    val aGate = gates[a] ?: addLogicGate(a, logic.getValue(a))
    val bGate = gates[b] ?: addLogicGate(b, logic.getValue(b))
    val newGate = when (op) {
        "AND" -> AndGate(aGate, bGate)
        "OR" -> OrGate(aGate, bGate)
        "XOR" -> XorGate(aGate, bGate)
        else -> throw IllegalArgumentException(definition)
    }
    gates[name] = newGate
    return newGate
}
for ((name, definition) in logic.entries) {
    addLogicGate(name, definition)
}
val zGates = gates.entries.filter { it.key.startsWith("z") }.sortedBy { it.key }.map { it.value.value }
println(zGates.mapIndexed { i, it -> if (it) 1L shl i else 0L }.reduce { acc, it -> acc or it  })
