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
    val zeroIndex = state.indexOfFirst { it.value == 0L }
    return listOf(1000, 2000, 3000).sumOf { state[(zeroIndex + it).mod(state.size)].value }
}

val lines = java.io.File(args[0]).readLines()
val values = lines.map { it.toLong() }
println(decrypt(values, 1))
println(decrypt(values.map { it * 811589153L }, 10))
