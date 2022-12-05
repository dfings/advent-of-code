#!/usr/bin/env kotlin

typealias Stack = ArrayDeque<Char> // Top = last
fun Stack.push(value: Char) = add(value)
fun Stack.push(values: List<Char>) = addAll(values)
fun Stack.pop() = removeLast()
fun Stack.pop(count: Int) = takeLast(count).also { repeat(count) { removeLast() } }

typealias Stacks = List<ArrayDeque<Char>> 
fun Stacks.topCrates() = map { it.last() }.joinToString("")
fun Stacks.copy() = map { ArrayDeque<Char>(it) }

data class Instruction(val count: Int, val from: Int, val to: Int) {
    fun execute(stacks: Stacks) = repeat(count) { stacks[to].push(stacks[from].pop()) }
    fun executeBulk(stacks: Stacks) = stacks[to].push(stacks[from].pop(count))
}

val lines = java.io.File(args[0]).readLines()
val buckets = (lines[0].length + 1) / 4
val initialStacks = (1..buckets).map { Stack() }
lines.filter { it.contains("[") }.forEach {
    it.chunked(4).forEachIndexed { i, crate ->
        if (crate.contains("[")) initialStacks[i].addFirst(crate[1])
    }
}

val instructions = lines.filter { it.contains("move") }.map { 
    val (count, from, to) = it.split("move ", " from ", " to ").drop(1)
    Instruction(count.toInt(), from.toInt() - 1, to.toInt() - 1)
}

val stacks1 = initialStacks.copy().apply { instructions.forEach { it.execute(this) } }
println(stacks1.topCrates())

val stacks2 = initialStacks.copy().apply { instructions.forEach { it.executeBulk(this) } }
println(stacks2.topCrates())
