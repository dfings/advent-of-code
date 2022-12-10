#!/usr/bin/env kotlin

class Device {
    var x = 1
    var cycle = 0
    var strength = 0
    val screen = MutableList(240) { '.' }

    fun addX(value: Int) {
        repeat(2) { tick() }
        x += value
    }

    fun tick() {
        screen[cycle] = if (cycle % 40 in x - 1..x + 1) '#' else '.'
        if ((++cycle + 20) % 40 == 0) {
            strength += cycle * x
        }
    }
}

val lines = java.io.File(args[0]).readLines()
val device = Device()
for (line in lines) {
    when (line) {
        "noop" -> device.tick()
        else -> device.addX(line.drop(5).toInt())
    }
}

println(device.strength)
device.screen.chunked(40).forEach { println(it.joinToString("")) }
