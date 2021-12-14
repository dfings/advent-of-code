#!/usr/bin/env kotlin

fun <T> Iterable<T>.histogram(): Map<T, Long> = 
    groupingBy { it }.eachCount().mapValues { it.value.toLong() }

fun <T> MutableMap<T, Long>.incrementKey(key: T, amount: Long) =
    compute(key) { _, v -> (v ?: 0L) + amount }

fun solve(initial: String, rules: Map<String, List<String>>, rounds: Int): Long {
    val histogram = initial.toList().histogram().toMutableMap()
    var polymer = initial.windowed(2).histogram().toMutableMap()
    repeat (rounds) {
        for ((pair, count) in polymer.toMap()) {
            val split: List<String>? = rules[pair]
            if (split != null) {
                // All instances of the given pair are replaced by the 2 new pairs.
                polymer.incrementKey(pair, -count)
                polymer.incrementKey(split[0], count)
                polymer.incrementKey(split[1], count)
                // The histogram increases by the middle element.
                histogram.incrementKey(split[0][1], count)
            }
        }
    }
    return histogram.maxOf { it.value } - histogram.minOf { it.value }
}

val lines = java.io.File(args[0]).readLines()
val initial = lines.first()
val rules = lines.drop(2).map { 
    it.split(" -> ").let { it[0] to listOf(it[0][0] + it[1], it[1] + it[0][1]) } 
}.toMap()

println(solve(initial, rules, 10))
println(solve(initial, rules, 40))
