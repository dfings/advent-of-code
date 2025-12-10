#!/usr/bin/env kotlin

data class Machine(val target: Int, val buttons: IntArray)
data class State(val value: Int, val cost: Int, val lastButton: Int)

fun String.parse(): Machine {
    val target = substring(indexOf("[") + 1, indexOf("]")).mapIndexed { i, it -> 
        if (it == '#') 1 shl i else 0
    }.reduce { a, b -> a or b }

    val buttons = substring(indexOf("("), lastIndexOf(")") + 1).split(" ").map {
        it.substring(1, it.lastIndex).split(",").map { 1 shl it.toInt() }.reduce { a, b -> a or b }
    }.toIntArray()

    return Machine(target, buttons)
}

fun Machine.solve(): Int {
    val minCost = mutableMapOf(0 to 0)
    val frontier = mutableSetOf(State(0, 0, 0))
    while (!frontier.isEmpty()) {
        val state = frontier.minBy { it.cost }
        frontier.remove(state)
        if (state.value == target) return state.cost
        for (button in buttons) {
            if (button != state.lastButton) {
                val newValue = state.value xor button
                val oldCost = minCost[newValue] ?: Int.MAX_VALUE
                val newCost = state.cost + 1
                if (newCost < oldCost) {
                    minCost[newValue] = newCost
                    frontier.add(State(newValue, newCost, button))
                }
            }
        }
    }
    throw IllegalStateException("Path not found")
}

fun solve(lines: List<String>) {
    val machines = lines.map { it.parse() }
    println(machines.sumOf { it.solve() })
}

solve(java.io.File(args[0]).readLines())
