#!/usr/bin/env kotlin

fun <T> Iterable<T>.histogram(): Map<T, Long> = 
    groupingBy { it }.eachCount().mapValues { it.value.toLong() }

fun <T> MutableMap<T, Long>.increment(key: T, amount: Long) =
    compute(key) { _, v -> (v ?: 0L) + amount }

fun solve(initial: String, rules: Map<String, List<String>>, rounds: Int): Long {
    val elementCounts = initial.toList().histogram().toMutableMap()
    val pairCounts = initial.windowed(2).histogram().toMutableMap()
    repeat (rounds) {
        for ((pair, count) in pairCounts.toMap()) {
            val split: List<String>? = rules[pair]
            if (split != null) {
                // All instances of the given pair are replaced by the 2 new pairs.
                pairCounts.increment(pair, -count)
                pairCounts.increment(split[0], count)
                pairCounts.increment(split[1], count)
                // The element histogram increases by the middle element.
                elementCounts.increment(split[0][1], count)
            }
        }
    }
    return elementCounts.maxOf { it.value } - elementCounts.minOf { it.value }
}

val lines = java.io.File(args[0]).readLines()
val initial = lines.first()
val rules = lines.drop(2).map { 
    it.split(" -> ").let { it[0] to listOf(it[0][0] + it[1], it[1] + it[0][1]) } 
}.toMap()

println(solve(initial, rules, 10))
println(solve(initial, rules, 40))
