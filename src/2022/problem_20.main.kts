#!/usr/bin/env kotlin

fun decrypt(values: List<Long>, rounds: Int): Long {
    val indexedValues = values.withIndex()
    val state = indexedValues.toMutableList()
    repeat (rounds) {
        for (v in indexedValues) {
            val index = state.indexOfFirst { it.index == v.index }
            state.removeAt(index)
            state.add((index + v.value).mod(state.size), v)
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
