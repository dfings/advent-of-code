#!/usr/bin/env kotlin

import kotlin.math.abs
import kotlin.math.sign

fun MutableList<IndexedValue<Int>>.move(v: IndexedValue<Int>) {
    var index = indexOfFirst { it.index == v.index }
    repeat(abs(v.value)) {
        val newIndex = (index + v.value.sign).mod(size)
        set(index, get(newIndex))
        set(newIndex, v)
        index = newIndex
    }
}

val lines = java.io.File(args[0]).readLines()
val ring = lines.map { it.toInt() }.withIndex()
val state = ring.toMutableList()
for (value in ring) {
    state.move(value)
}

val indexOfZero = state.indexOfFirst { it.value == 0 }
println(state[(indexOfZero + 1000).mod(state.size)].value +
        state[(indexOfZero + 2000).mod(state.size)].value +
        state[(indexOfZero + 3000).mod(state.size)].value)
