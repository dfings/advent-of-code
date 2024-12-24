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

val logicPattern = Regex("""(\w+) (\w+) (\w+) ->""")

class Device {
    val wires = mutableMapOf<String, Wire>()
    lateinit var initialLogic: Map<String, String>

    fun loadInput(input: List<String>) {
        for (definition in input) {
            val (name, value) = definition.split(": ")
            wires[name] = Wire(name, InputGate(value == "1"))
        }
    }

    fun loadLogic(input: List<String>) {
        initialLogic = input.map { it.takeLast(3) to it }.toMap()
        for ((name, definition) in initialLogic.entries) {
            addLogic(name, definition)
        }
    }

    fun addLogic(name: String, definition: String): Wire {
        wires[name]?.let { return it }
        val (a, op, b) = logicPattern.find(definition)!!.destructured
        val aWire = wires[a] ?: addLogic(a, initialLogic.getValue(a))
        val bWire = wires[b] ?: addLogic(b, initialLogic.getValue(b))
        val newWire = when (op) {
            "AND" -> Wire(name, AndGate(aWire, bWire))
            "OR" -> Wire(name, OrGate(aWire, bWire))
            "XOR" -> Wire(name, XorGate(aWire, bWire))
            else -> throw IllegalArgumentException(definition)
        }
        wires[name] = newWire
        return newWire
    }

    fun getOutput(): Long =
        getSortedWires("z")
            .mapIndexed { i, it -> if (it.input.value) 1L shl i else 0L }
            .reduce { acc, it -> acc or it }

    fun getSortedWires(wirePrefix: String): List<Wire> =
        wires.entries.filter { it.key.startsWith(wirePrefix) }
            .sortedBy { it.key }.map { it.value }

    fun setLong(wirePrefix: String, value: Long) {
        val input = getSortedWires(wirePrefix).map { it.input as InputGate }
        for ((i, gate) in input.withIndex()) {
            gate.value = (value and (1L shl i)) != 0L
        }
    }
}


val lines = java.io.File(args[0]).readLines()

val device = Device()
device.loadInput(lines.takeWhile { it != "" })
device.loadLogic(lines.dropWhile { it != "" }.drop(1))

println(device.getOutput())
