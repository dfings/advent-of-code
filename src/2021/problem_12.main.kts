#!/usr/bin/env kotlin

data class Edge(val src: String, val dst: String)

data class Path(val edges: List<Edge> = listOf()) {
    operator fun plus(edge: Edge) = Path(edges + edge)
    fun canAdd(edge: Edge) =
        edge.dst.all(Char::isUpperCase) || (edge.dst != "start" && edges.none { it.dst == edge.dst })
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
        val nextEdges = edges.filter { edge.dst == it.src && currentPath.canAdd(it) }
        return nextEdges.map { findAllPaths(it, currentPath + edge) }.flatten()
    }
}

fun String.toEdges() = split("-").let { listOf(Edge(it[0], it[1]), Edge(it[1], it[0])) }
val graph = Graph(java.io.File(args[0]).readLines().flatMap { it.toEdges() })
println(graph.findAllPaths().size)
