#!/usr/bin/env kotlin


val pattern = Regex("""(\w+) would (gain|lose) (\d+) happiness units by sitting next to (\w+).""")

val lines = java.io.File(args[0]).readLines()

val deltas = mutableMapOf<String, MutableMap<String, Int>>()
for (line in lines) {
    val (first, gainLost, cost, second) = pattern.find(line)!!.destructured
    val sign = if (gainLost == "gain") 1 else -1
    deltas.getOrPut(first) { mutableMapOf<String, Int>() }.put(second, sign * cost.toInt())
}

val costs = mutableMapOf<String, MutableMap<String, Int>>()
for (person1 in deltas.keys) {
    costs[person1] = mutableMapOf<String, Int>()
    for (person2 in deltas.keys.filter { it != person1 }) {
        costs.getValue(person1).put(
            person2, 
            deltas.getValue(person1).getValue(person2) + deltas.getValue(person2).getValue(person1)
        )
    }
}

fun findMaxHappiness(path: List<String>, cost: Int): Int {
    if (path.size == costs.size) return cost + costs.getValue(path.last()).getValue(path.first())
    val next = costs.keys - path
    val nextCost = costs.getValue(path.last())
    return next.maxOf { findMaxHappiness(path + it, cost + nextCost.getValue(it)) }
}

println(costs.keys.maxOf { findMaxHappiness(listOf(it), 0) })

costs["me"] = mutableMapOf<String, Int>()
for (person in deltas.keys) {
    costs.getValue(person).put("me", 0)
    costs.getValue("me").put(person, 0)
}
println(costs.keys.maxOf { findMaxHappiness(listOf(it), 0) })
