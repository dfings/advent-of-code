#!/usr/bin/env kotlin

enum class Pulse { HIGH, LOW, NONE }

interface Module {
    val name: String
    fun receive(from: String, pulse: Pulse): Pulse
}

data class Relay(override val name: String) : Module {
    override fun receive(from: String, pulse: Pulse) = pulse
}

data class FlipFlop(override val name: String, var on: Boolean = false) : Module {
    override fun receive(from: String, pulse: Pulse): Pulse {
        if (pulse == Pulse.LOW) {
            on = !on
            return if (on) Pulse.HIGH else Pulse.LOW
        }
        return Pulse.NONE
    }
}

data class Conjunction(override val name: String, val lastPulses: MutableMap<String, Pulse>) : Module {
    override fun receive(from: String, pulse: Pulse): Pulse {
        lastPulses[from] = pulse
        return if (lastPulses.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH
    }
}

data class Observer(val highSeen: MutableSet<String> = mutableSetOf<String>()) : Module {
    override val name = "observer"
    override fun receive(from: String, pulse: Pulse): Pulse {
        if (pulse == Pulse.HIGH) highSeen += from
        return Pulse.NONE
    }
}

data class Machine(val modules: Map<String, Module>, val cables: Map<String, List<String>>)
data class Signal(val from: String, val pulse: Pulse, val to: String)

fun Machine.pressButton() : Pair<Long, Long> {
    var lowCount = 0L
    var highCount = 0L
    val actionQueue = ArrayDeque(listOf(Signal("button", Pulse.LOW, "broadcaster")))
    while (!actionQueue.isEmpty()) {
        val (from, pulse, to) = actionQueue.removeFirst()
        when (pulse) {
            Pulse.LOW -> lowCount++
            Pulse.HIGH -> highCount++
            Pulse.NONE -> continue
        }
        val module = modules.getValue(to)
        val outputPulse = module.receive(from, pulse)
        for (next in cables[to] ?: emptyList()) {
            actionQueue += Signal(to, outputPulse, next)
        }
    }
    return lowCount to highCount
}

fun parseMachine(lines: List<String>, withObserver: Boolean = false): Machine {
    val modules = mutableMapOf<String, Module>()
    val cables = mutableMapOf<String, List<String>>()
    for (line in lines) {
        val (input, output) = line.split(" -> ")
        val module = when {
            input.startsWith("%") -> FlipFlop(input.drop(1))
            input.startsWith("&") -> Conjunction(input.drop(1), mutableMapOf<String, Pulse>())
            else -> Relay(input)
        }
        modules[module.name] = module
        cables[module.name] = output.split(", ")
    }
    for ((input, outputs) in cables.entries) {
        for (output in outputs) {
            val module = modules[output]
            if (module == null) {
                modules[output] = Relay(output)
            } else if (module is Conjunction) {
                module.lastPulses[input] = Pulse.LOW
            }
        }
    }
    if (withObserver) {
        val rxInput = cables.entries.first { "rx" in it.value }.key
        val rxInputInputs = cables.entries.filter { rxInput in it.value }.map { it.key }
        val observer = Observer()
        modules[observer.name] = observer
        for (input in rxInputInputs) {
            cables[input] = cables.getValue(input) + observer.name
        }
    }
    return Machine(modules, cables)
}

fun solve1(lines: List<String>) {
    var machine = parseMachine(lines)
    var lowCount = 0L
    var highCount = 0L
    repeat (1000) {
        val output = machine.pressButton()
        lowCount += output.first
        highCount += output.second
    }
    println("${lowCount * highCount}")
}

fun solve2(lines: List<String>) {
    var machine = parseMachine(lines, withObserver = true)
    val observer = machine.modules.getValue("observer") as Observer
    val observing = machine.cables.entries.count { "observer" in it.value }
    val cycleTime = mutableMapOf<String, Long>()
    var i = 0L
    while (cycleTime.size != observing) {
        i++
        machine.pressButton()
        for (seen in observer.highSeen) {
            if (seen !in cycleTime) {
                cycleTime[seen] = i
            }
        }
    }
    println(cycleTime.values.reduce(Long::times))
}


fun solve(lines: List<String>) {
    solve1(lines)
    solve2(lines)
}

solve(java.io.File(args[0]).readLines())
