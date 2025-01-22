#!/usr/bin/env kotlin

import kotlin.math.abs
import kotlin.math.sign

fun MutableList<IndexedValue<Long>>.move(v: IndexedValue<Long>) {
    var index = indexOfFirst { it.index == v.index }
    repeat(abs(v.value).mod(size - 1)) {
        val newIndex = (index + v.value.sign).mod(size)
        set(index, get(newIndex))
        set(newIndex, v)
        index = newIndex
    }
}

fun decrypt(values: List<Long>, rounds: Int): Long {
    val state = values.withIndex().toMutableList()
    repeat (rounds) {
        for (v in values.withIndex()) {
            state.move(v)
        }
    }
    val indexOfZero = state.indexOfFirst { it.value == 0L }
    fun getValue(i: Int) = state[(indexOfZero + i).mod(state.size)].value
    return getValue(1000) + getValue(2000) + getValue(3000)
}

val lines = java.io.File(args[0]).readLines()
val values = lines.map { it.toLong() }
println(decrypt(values, 1))
println(decrypt(values.map { it * 811589153L }, 10))
