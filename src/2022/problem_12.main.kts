#!/usr/bin/env kotlin

class Vertex(val x: Int, val y: Int, val code: Char, var distance: Int = Int.MAX_VALUE) {
    val height: Int
      get() = when(code) {
        'S' -> 0
        'E' -> 26
        else -> code - 'a'
      }
}

class Graph(val vertexes: List<List<Vertex>>) {
    val xMax = vertexes[0].lastIndex
    val yMax = vertexes.lastIndex

    fun computeShortestPath(): Int {
        val start = vertexes.flatten().single { it.code == 'S' }
        val end = vertexes.flatten().single { it.code == 'E' }
        start.distance = 0
        val frontier = mutableSetOf<Vertex>(start)
        var count = 0
        while (!frontier.isEmpty()) {
            val vertex = frontier.minByOrNull { it.distance }!!
            frontier.remove(vertex)
            vertex.neighbors().filter { it.distance == Int.MAX_VALUE }.forEach { 
                it.distance = vertex.distance + 1
                frontier.add(it)
            }
        }
        return end.distance
    }

    fun Vertex.neighbors() = listOfNotNull(
        vertexAt(x - 1, y), vertexAt(x + 1, y), 
        vertexAt(x, y - 1), vertexAt(x, y + 1)
    ).filter { height + 1 >= it.height }
    
    fun vertexAt(x: Int, y: Int) =
        if (x < 0 || x > xMax || y < 0 || y > yMax) null else vertexes[y][x]
}

val lines = java.io.File(args[0]).readLines()
val graph = Graph(lines.mapIndexed { y, line -> line.mapIndexed { x, code -> Vertex(x, y, code) } })

println(graph.computeShortestPath())
