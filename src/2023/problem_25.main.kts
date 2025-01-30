#!/usr/bin/env kotlin

fun parse(lines: List<String>): Map<String, List<String>> {
    val graph = mutableMapOf<String, MutableList<String>>()
    for (line in lines) {
        val (a, bs) = line.split(": ").let { it[0] to it[1].split(" ")}
        for (b in bs) {
            graph.getOrPut(a) { mutableListOf<String>() }.add(b)
            graph.getOrPut(b) { mutableListOf<String>() }.add(a)
        }
    }
    return graph
}

fun solve(lines: List<String>) {
    while (true) {
        val graph = parse(lines).toMutableMap()
        val counts = graph.keys.associateWith { 1 }.toMutableMap()
        while (graph.keys.size > 2) {
            val a = graph.keys.random()
            val b = graph.getValue(a).random()
            val ab = "$a-$b"
            counts[ab] = (counts.remove(a) ?: 0) + (counts.remove(b) ?: 0)
            graph[ab] = graph.getValue(a).filter { it != b } + graph.getValue(b).filter { it != a }
            graph.remove(a)?.forEach { c ->
                graph[c] = graph.getValue(c).map { if (it == a) ab else it }
            }
            graph.remove(b)?.forEach { c ->
                graph[c] = graph.getValue(c).map { if (it == b) ab else it }
            }
        }
        if (graph.entries.first().value.size == 3) {
            println(counts.values.reduce(Int::times))
            break
        }
    }
}

solve(java.io.File(args[0]).readLines())
