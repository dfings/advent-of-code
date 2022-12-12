#!/usr/bin/env kotlin

class Vertex(val x: Int, val y: Int, val code: Char) {
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

    fun findShortedPath(target: Char): Int? {
        val end = vertexes.flatten().single { it.code == 'E' }
        val frontier = ArrayDeque<Pair<Vertex, Int>>(listOf(end to 0))
        val visited = mutableSetOf<Vertex>(end)
        while (!frontier.isEmpty()) {
            val (vertex, distance) = frontier.removeFirst()
            if (vertex.code == target) return distance
            vertex.neighbors().filter { it.height + 1 >= vertex.height }.forEach { 
                if (visited.add(it)) frontier.add(it to distance + 1)
            }
        }
        return null
    }

    fun Vertex.neighbors() = listOfNotNull(
        vertexAt(x - 1, y), vertexAt(x + 1, y), 
        vertexAt(x, y - 1), vertexAt(x, y + 1)
    )
    
    fun vertexAt(x: Int, y: Int) =
        if (x < 0 || x > xMax || y < 0 || y > yMax) null else vertexes[y][x]
}

val lines = java.io.File(args[0]).readLines()
val graph = Graph(lines.mapIndexed { y, line -> line.mapIndexed { x, code -> Vertex(x, y, code) } })

println(graph.findShortedPath('S'))
println(graph.findShortedPath('a'))
