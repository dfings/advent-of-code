#!/usr/bin/env kotlin

fun solve(initial: String, rules: Map<String, List<String>>, rounds: Int): Long {
    val histogram = initial.groupingBy { it }.eachCount().mapValues { it.value.toLong() }.toMutableMap()
    var polymer = initial.windowed(2).groupingBy { it }.eachCount().mapValues { it.value.toLong() }
    repeat (rounds) {
        val updated = polymer.toMutableMap()
        for ((pair, count) in polymer) {
            val split = rules[pair]
            if (split != null) {
                updated.compute(pair) { _, v -> (v ?: 0) - count }
                updated.compute(split[0]) { _, v -> (v ?: 0) + count } 
                updated.compute(split[1]) { _, v -> (v ?: 0) + count } 
                histogram.compute(split[0][1]) { _, v -> (v ?: 0) + count }
            }
        }
        polymer = updated.toMap()
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
