#!/usr/bin/env kotlin

data class Edge(val src: String, val dst: String)

data class Path(val edges: List<Edge> = listOf()) {
    operator fun plus(edge: Edge) = Path(edges + edge)
}

class Graph(val edges: List<Edge>) {
    fun findAllPaths(): List<Path> =
        edges.filter { it.src == "start" }.map { findAllPaths(it, Path()) }.flatten()

    fun findAllPaths(
        edge: Edge, 
        currentPath: Path
    ): List<Path> {
        if (edge.dst == "end") {
            return listOf(currentPath + edge)
        }
        val nextEdges = edges.filter { 
            edge.dst == it.src && 
            (edge.dst.all(Char::isUpperCase) || 
            currentPath.edges.count { it.dst == edge.dst } == 0)
        }
        return nextEdges.map { findAllPaths(it, currentPath + edge) }.flatten()
    }
}

fun String.toEdges() = split("-").let { listOf(Edge(it[0], it[1]), Edge(it[1], it[0])) }
val lines = java.io.File(args[0]).readLines()
val graph = Graph(lines.flatMap { it.toEdges() }.filter { it.src != "end" && it.dst != "start"})
println(graph.findAllPaths().size)
