#!/usr/bin/env kotlin

data class Cave(val name: String) {
    val isSmall = name.all(Char::isLowerCase)
}

data class Edge(val src: Cave, val dst: Cave)

data class Path(val caves: List<Cave>) {
    operator fun plus(cave: Cave) = Path(caves + cave)
    val smallCavesList: List<Cave> = caves.filter { it.isSmall }
    val smallCavesSet: Set<Cave> = smallCavesList.toSet()
}

class Graph(val edges: List<Edge>) {
    fun findAllPaths(
        cave: Cave = Cave("start"), 
        currentPath: Path = Path(emptyList()),
        canVisitSmallCave: Path.(Cave) -> Boolean,
    ): List<Path> = when {
        cave.name == "end" -> listOf(currentPath + cave)
        cave.isSmall && !currentPath.canVisitSmallCave(cave) -> emptyList()
        else -> edges.filter { cave == it.src }.map { 
            findAllPaths(it.dst, currentPath + cave, canVisitSmallCave) 
        }.flatten()
    }
}

fun String.toEdges() = split("-").map { Cave(it) }.let {
    listOf(Edge(it[0], it[1]), Edge(it[1], it[0])).filter { 
        it.src.name != "end" && it.dst.name != "start"
    } 
}
val graph = Graph(java.io.File(args[0]).readLines().flatMap { it.toEdges() })

println(graph.findAllPaths { !smallCavesSet.contains(it) }.size)
println(graph.findAllPaths { 
    smallCavesSet.size == smallCavesList.size || !smallCavesSet.contains(it) 
}.size)
