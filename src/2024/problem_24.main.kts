#!/usr/bin/env kotlin

sealed interface Gate {
    val value: Boolean
}

data class Wire(val name: String, var input: Gate) {}

data class InputGate(override var value: Boolean) : Gate

data class AndGate(var a: Wire, var b: Wire) : Gate {
    override val value: Boolean
        get() = a.input.value && b.input.value
}

data class OrGate(var a: Wire, var b: Wire) : Gate {
    override val value: Boolean
        get() = a.input.value || b.input.value
}

data class XorGate(var a: Wire, var b: Wire) : Gate {
    override val value: Boolean
        get() = a.input.value xor b.input.value
}

fun Map<String, Wire>.getSortedWires(wirePrefix: String): List<Wire> =
    entries.filter { it.key.startsWith(wirePrefix) }
        .sortedBy { it.key }.map { it.value }

fun Map<String, Wire>.setLong(wirePrefix: String, value: Long) {
    val input = getSortedWires(wirePrefix).map { it.input as InputGate }
    for ((i, gate) in input.withIndex()) {
        gate.value = (value and (1L shl i)) != 0L
    }
}

fun Map<String, Wire>.toLong(wirePrefix: String) = 
    getSortedWires(wirePrefix)
        .mapIndexed { i, it -> if (it.input.value) 1L shl i else 0L }
        .reduce { acc, it -> acc or it }

val lines = java.io.File(args[0]).readLines()
val wires = mutableMapOf<String, Wire>()

for (line in lines.takeWhile { it != "" }) {
    val (name, value) = line.split(": ")
    wires[name] = Wire(name, InputGate(value == "1"))
}

val logicPattern = Regex("""(\w+) (\w+) (\w+) ->""")
val logic = lines.dropWhile { it != "" }.drop(1).map { it.takeLast(3) to it }.toMap()
fun addLogicWireAndGate(name: String, definition: String): Wire {
    wires[name]?.let { return it }
    val (a, op, b) = logicPattern.find(definition)!!.destructured
    val aWire = wires[a] ?: addLogicWireAndGate(a, logic.getValue(a))
    val bWire = wires[b] ?: addLogicWireAndGate(b, logic.getValue(b))
    val newWire = when (op) {
        "AND" -> Wire(name, AndGate(aWire, bWire))
        "OR" -> Wire(name, OrGate(aWire, bWire))
        "XOR" -> Wire(name, XorGate(aWire, bWire))
        else -> throw IllegalArgumentException(definition)
    }
    wires[name] = newWire
    return newWire
}
for ((name, definition) in logic.entries) {
    addLogicWireAndGate(name, definition)
}

println(wires.toLong("z"))
