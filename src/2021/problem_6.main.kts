#!/usr/bin/env kotlin

fun runGenerations(start: List<Long>, generations: Int): List<Long> {
    val state = ArrayDeque(start)
    repeat (generations) { 
        val spawn = state.removeFirst()
        state.add(spawn)
        state[6] += spawn
    }
    return state.toList()
}
    
val start = MutableList(9) { 0L }
java.io.File(args[0]).readLines().first().split(",").forEach { start[it.toInt()]++ }

println(runGenerations(start, 80).sum())
println(runGenerations(start, 256).sum())
