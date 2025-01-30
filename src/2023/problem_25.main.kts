#!/usr/bin/env kotlin

class Graph {
    private val map = mutableMapOf<String, MutableList<String>>()

    fun nodes() = map.keys
    fun edges(a: String) = map[a] ?: emptyList()

    fun addEdge(a: String, b: String) {
        map.getOrPut(a) { mutableListOf<String>() }.add(b)
        map.getOrPut(b) { mutableListOf<String>() }.add(a)   
    }

    fun removeNode(a: String) {
        for (c in map.remove(a) ?: emptyList()) {
            map.getValue(c).removeAll { it == a }
        }
    }
}

fun parse(lines: List<String>): Graph {
    val graph = Graph()
    for (line in lines) {
        val (a, bs) = line.split(": ").let { it[0] to it[1].split(" ")}
        for (b in bs) {
            graph.addEdge(a, b)
        }
    }
    return graph
}

fun solve(lines: List<String>) {
    while (true) {
        val graph = parse(lines)
        val counts = graph.nodes().associateWith { 1 }.toMutableMap()
        while (graph.nodes().size > 2) {
            val a = graph.nodes().random()
            val b = graph.edges(a).random()
            val ab = "$a-$b"
            counts[ab] = (counts.remove(a) ?: 0) + (counts.remove(b) ?: 0)
            val connected = graph.edges(a) + graph.edges(b)
            graph.removeNode(a)
            graph.removeNode(b)
            for (c in connected.filter { it != a && it != b }) {
                graph.addEdge(ab, c)
            }
        }
        if (graph.edges(graph.nodes().first()).size == 3) {
            println(counts.values.reduce(Int::times))
            break
        }
    }
}

solve(java.io.File(args[0]).readLines())
