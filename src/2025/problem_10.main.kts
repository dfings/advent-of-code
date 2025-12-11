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

data class Machine2(val target: List<Int>, val buttons: List<List<Int>>, val targetOrder: List<Int>)
fun String.parse2(): Machine2 {
    val target = substring(indexOf("{") + 1, lastIndex).split(",").map { it.toInt() }
    val buttons = substring(indexOf("("), lastIndexOf(")") + 1).split(" ").map {
        it.substring(1, it.lastIndex).split(",").map { it.toInt() }
    }

    var buttonCount = target.indices.map { i -> buttons.count { i in it } }
    val targetOrder = buttonCount.withIndex().sortedWith(compareBy({ it.value}, {-target[it.index] })).map { it.index }
    return Machine2(target, buttons, targetOrder)
}

val cache = mutableMapOf<List<Int>, Int>()
fun Machine2.solve(): Int {
    cache.clear()
    return solve(
        target.map { 0 }, 
        0, 
        targetOrder[0], 
        buttons.filter { targetOrder[0] in it }.sortedBy { -it.size }, 
        buttons.filter { targetOrder[0] !in it },
    )
}

fun Machine2.solve(
    current: List<Int>, 
    cost: Int, 
    currentTarget: Int, 
    currentButtons: List<List<Int>>, 
    remainingButtons: List<List<Int>>
): Int = cache.getOrPut(current) {
    currentButtons.indices.minOfOrNull { index ->
        val button = currentButtons[index]
        val newValue = current.toMutableList()
        for (light in button) {
            newValue[light]++
        }
        val newButtons = if (index == 0) currentButtons else currentButtons.drop(index)
        when {
            newValue == target -> cost + 1
            newValue.indices.any { i -> newValue[i] > target[i] } -> 100_000
            newValue[currentTarget] == target[currentTarget] -> {
                val newTarget = targetOrder[targetOrder.indexOf(currentTarget) + 1]
                solve(
                    newValue,
                    cost + 1,
                    newTarget,
                    remainingButtons.filter { newTarget in it }.sortedBy { -it.size },
                    remainingButtons.filter { newTarget !in it },
                )
            }
            else -> solve(newValue, cost + 1, currentTarget, newButtons, remainingButtons)
        }
    } ?: 100_000
}

fun solve(lines: List<String>) {
    val machines1 = lines.map { it.parse1() }
    println(machines1.sumOf { it.solve() })

    val machines2 = lines.map { it.parse2() }
    println(machines2.sumOf { 
        println(it)
        it.solve() 
    })
}

solve(java.io.File(args[0]).readLines())
