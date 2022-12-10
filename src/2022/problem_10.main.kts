#!/usr/bin/env kotlin

class Cpu {
    var register = 1
    var clock = 0
    val strengths = mutableListOf<Int>()
    val screen = MutableList(240) { "." }

    fun addX(x: Int) {
        repeat(2) { tick() }
        register += x
    }

    fun tick() {
        val position = clock % 240
        screen[position] = if (position % 40 in register - 1..register + 1) "#" else "."
        clock += 1
        if ((clock + 20) % 40 == 0) {
            strengths.add(clock * register)
        }
    }
}

val lines = java.io.File(args[0]).readLines()
val cpu = Cpu()
for (line in lines) {
    when (line) {
        "noop" -> cpu.tick()
        else -> cpu.addX(line.drop(5).toInt())
    }
}

println(cpu.strengths.sum())
for (line in cpu.screen.chunked(40)) {
    println(line.joinToString(""))
}
