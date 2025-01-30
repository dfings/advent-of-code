#!/usr/bin/env kotlin

class Graph {
    private val map = mutableMapOf<String, MutableList<String>>()

    fun nodes() = map.keys
    fun edges(a: String) = map[a] ?: emptyList()

    fun addEdge(a: String, b: String) {
        map.getOrPut(a) { mutableListOf<String>() }.add(b)
        map.getOrPut(b) { mutableListOf<String>() }.add(a)   
    }

    fun removeNode(a: String): List<String> {
        val edges = map.remove(a) ?: emptyList()
        for (c in edges) {
            map.getValue(c).removeAll { it == a }
        }
        return edges
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
        while (graph.nodes().size > 2) {
            val a = graph.nodes().random()
            val b = graph.edges(a).random()
            val ab = "$a-$b"
            for (c in graph.removeNode(a) + graph.removeNode(b)) {
                if (c != a && c != b) {            
                    graph.addEdge(ab, c)
                }
            }
        }
        if (graph.edges(graph.nodes().first()).size == 3) {
            println(graph.nodes().map { it.split("-").size }.reduce(Int::times))
            break
        }
    }
}

solve(java.io.File(args[0]).readLines())
