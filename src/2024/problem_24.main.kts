#!/usr/bin/env kotlin

sealed interface Gate {
    val value: Boolean
}

data class Wire(val name: String, var input: Gate) {
    val value: Boolean
        get() = input.value
}

data class InputGate(override var value: Boolean) : Gate

interface BinaryGate : Gate {
    val input1: Wire
    val input2: Wire
    val key: Set<String>
        get() = setOf(input1.name, input2.name)
}

data class AndGate(override val input1: Wire, override val input2: Wire) : BinaryGate {
    override val value: Boolean
        get() = input1.value && input2.value
}

data class OrGate(override val input1: Wire, override val input2: Wire) : BinaryGate {
    override val value: Boolean
        get() = input1.value || input2.value
}

data class XorGate(override val input1: Wire, override val input2: Wire) : BinaryGate {
    override val value: Boolean
        get() = input1.value xor input2.value
}

val logicPattern = Regex("""(\w+) (\w+) (\w+) -> (\w+)""")

class Device {
    val wires = java.util.TreeMap<String, Wire>()
    lateinit var initialLogic: Map<String, String>
    lateinit var xInput: List<InputGate>
    lateinit var yInput: List<InputGate>
    lateinit var zWires: List<Wire>
    lateinit var andOutput: Map<Set<String>, Wire>
    lateinit var orOutput: Map<Set<String>, Wire>
    lateinit var xorOutput: Map<Set<String>, Wire>
    lateinit var finalCarry: Array<String>

    fun loadInput(input: List<String>) {
        for (definition in input) {
            val (name, value) = definition.split(": ")
            wires[name] = Wire(name, InputGate(value == "1"))
        }
        xInput = wires.entries.filter { it.key[0] == 'x' }.map { it.value.input as InputGate }
        yInput = wires.entries.filter { it.key[0] == 'y' }.map { it.value.input as InputGate }
    }

    fun loadLogic(input: List<String>) {
        initialLogic = input.associateBy { it.takeLast(3) }
        for ((name, definition) in initialLogic.entries) {
            addLogic(name, definition)
        }
        zWires = wires.entries.filter { it.key[0] == 'y' }.map { it.value }
        finalCarry = Array(zWires.size) { "" }
        reindex()
    }

    private fun reindex() {
        val gates = wires.values.map { it.input }
        val wiresByInputGate = java.util.IdentityHashMap<Gate, Wire>()
        wires.values.associateByTo(wiresByInputGate) { it.input }
        fun <T : BinaryGate> List<T>.makeWireMap() =
            associateBy { it.key }.mapValues { wiresByInputGate.getValue(it.value) }
        andOutput = gates.filterIsInstance<AndGate>().makeWireMap()
        orOutput = gates.filterIsInstance<OrGate>().makeWireMap()
        xorOutput = gates.filterIsInstance<XorGate>().makeWireMap()
    }

    private fun addLogic(name: String, definition: String): Wire {
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

    fun fixErrors() {
        for (i in 0..zWires.lastIndex - 1) {
            analyze(i)
        }
    }

    private fun analyze(bitIndex: Int) {
        if (bitIndex == 0) {
            if (xorOutput.getValue(setOf("x00", "y00")).name != "z00") {
                throw IllegalArgumentException("Swap z00 into x00 XOR y00")
            }
            println("x00 XOR y00 -> z00")
            finalCarry[0] = andOutput.getValue(setOf("x00", "y00")).name
            println("x00 AND y00 -> ${finalCarry[0]}")
        } else {
            val i = if (bitIndex < 10) "0$bitIndex" else "$bitIndex"
            val previousFinalCarry = finalCarry[bitIndex - 1]
            val firstResult = xorOutput.getValue(setOf("x$i", "y$i")).name
            println("x$i XOR y$i -> $firstResult")
            val firstCarry = andOutput.getValue(setOf("x$i", "y$i")).name
            println("x$i AND y$i -> $firstCarry")
            if (xorOutput.getValue(setOf(firstResult, previousFinalCarry)).name != "z$i") {
                throw IllegalArgumentException("Swap z$i into x$i XOR y$i")
            }
            println("$firstResult XOR $previousFinalCarry -> z$i")
            val secondCarry = andOutput.getValue(setOf("$firstResult", "$previousFinalCarry")).name
            println("$firstResult AND $previousFinalCarry -> $secondCarry")
            finalCarry[bitIndex] = orOutput.getValue(setOf("$firstCarry", "$secondCarry")).name
            println("$firstCarry OR $secondCarry -> ${finalCarry[bitIndex]}")
        }
        println()
    }

    fun setInput(x: Long, y: Long) {
        setLong(xInput, x)
        setLong(yInput, y)
    }

    private fun setLong(gates: List<InputGate>, value: Long) {
        for ((i, gate) in gates.withIndex()) {
            gate.value = (value and (1L shl i)) != 0L
        }
    }

    fun getOutput(): Long = zWires
        .mapIndexed { i, it -> if (it.input.value) 1L shl i else 0L }
        .reduce { acc, it -> acc or it }
    
    fun isCorrect(x: Long, y: Long): Boolean {
        setInput(x, y)
        return getOutput() == x + y
    }
}

val lines = java.io.File(args[0]).readLines()
val logicLines = lines.dropWhile { it != "" }.drop(1)

val device = Device()
device.loadInput(lines.takeWhile { it != "" })
device.loadLogic(logicLines)

println(device.getOutput())
device.fixErrors()
