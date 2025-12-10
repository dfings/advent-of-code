#!/usr/bin/env kotlin

import java.util.PriorityQueue

data class Machine1(val target: Int, val buttons: List<Int>)
data class State1(val value: Int, val cost: Int, val lastButton: Int)

fun String.parse1(): Machine1 {
    val target = substring(indexOf("[") + 1, indexOf("]")).mapIndexed { i, it -> 
        if (it == '#') 1 shl i else 0
    }.reduce { a, b -> a or b }

    val buttons = substring(indexOf("("), lastIndexOf(")") + 1).split(" ").map {
        it.substring(1, it.lastIndex).split(",").map { 1 shl it.toInt() }.reduce { a, b -> a or b }
    }

    return Machine1(target, buttons)
}

fun Machine1.solve(): Int {
    val minCost = mutableMapOf(0 to 0)
    val frontier = java.util.PriorityQueue<State1> { a, b -> a.cost.compareTo(b.cost) }
    frontier.add(State1(0, 0, 0))
    while (!frontier.isEmpty()) {
        val state = frontier.poll()
        if (state.value == target) return state.cost
        for (button in buttons) {
            if (button != state.lastButton) {
                val newValue = state.value xor button
                val oldCost = minCost[newValue] ?: Int.MAX_VALUE
                val newCost = state.cost + 1
                if (newCost < oldCost) {
                    minCost[newValue] = newCost
                    frontier.add(State1(newValue, newCost, button))
                }
            }
        }
    }
    throw IllegalStateException("Path not found")
}

data class Machine2(val target: List<Int>, val buttons: List<List<Int>>)
data class State2(val value: List<Int>, val cost: Int, val lastButtonIndex: Int)

fun String.parse2(): Machine2 {
    val target = substring(indexOf("{") + 1, lastIndex).split(",").map { it.toInt() }
    val buttons = substring(indexOf("("), lastIndexOf(")") + 1).split(" ").map {
        it.substring(1, it.lastIndex).split(",").map { it.toInt() }
    }.sortedBy { -it.sum() }

    return Machine2(target, buttons)
}

var solved = 0
fun Machine2.solve(): Int {
    val initialValue = List(target.size) { 0 }
    val minCost = mutableMapOf(initialValue to 0)
    val frontier = java.util.PriorityQueue<State2> { a, b -> a.cost.compareTo(b.cost) }
    frontier.add(State2(initialValue, 0, 0))
    while (!frontier.isEmpty()) {
        val state = frontier.minBy { it.cost }
        frontier.remove(state)
        if (state.value == target) { 
            return state.cost
        }
        for (index in (state.lastButtonIndex..buttons.lastIndex)) {
            val button = buttons[index]
            val newValue = state.value.toMutableList()
            for (light in button) {
                newValue[light]++
            }
            if (newValue.indices.any { i -> newValue[i] > target[i] }) continue
            val oldCost = minCost[newValue] ?: Int.MAX_VALUE
            val newCost = state.cost + 1
            if (newCost < oldCost) {
                minCost[newValue] = newCost
                frontier.add(State2(newValue, newCost, index))
            }
        }
    }
    throw IllegalStateException("Path not found")
}


fun solve(lines: List<String>) {
    val machines1 = lines.map { it.parse1() }
    println(machines1.sumOf { it.solve() })

    val machines2 = lines.map { it.parse2() }
    println(machines2.sumOf { it.solve() })
}

solve(java.io.File(args[0]).readLines())
