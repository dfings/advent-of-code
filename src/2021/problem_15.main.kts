#!/usr/bin/env kotlin

class Vertex(val x: Int, val y: Int, val weight: Int,  var distance: Int = Int.MAX_VALUE)

class Graph(val vertexes: List<List<Vertex>>) {
    val xMax = vertexes[0].lastIndex
    val yMax = vertexes.lastIndex

    fun computeShortestPath(): Int {
        vertexes[0][0].distance = 0
        val frontier = mutableSetOf<Vertex>(vertexes[0][0])
        while (!frontier.isEmpty()) {
            val vertex = frontier.minByOrNull { it.distance }!!
            vertex.neighbors().forEach { 
                val distance = vertex.distance + it.weight
                if (distance < it.distance) {
                    it.distance = distance
                    frontier.add(it)
                }
            }
            frontier.remove(vertex)
        }
        return vertexes[yMax][xMax].distance
    }

    fun Vertex.neighbors() = listOfNotNull(
        vertexAt(x - 1, y), vertexAt(x + 1, y), 
        vertexAt(x, y - 1), vertexAt(x, y + 1)
    )
    
    fun vertexAt(x: Int, y: Int) =
        if (x < 0 || x > xMax || y < 0 || y > yMax) null else vertexes[y][x]
}

fun List<List<Int>>.toGraph() = 
    Graph(mapIndexed { y, line ->  line.mapIndexed { x, weight -> Vertex(x, y, weight) } })

val lines = java.io.File(args[0]).readLines()
val originalMap = lines.map { it.map { it.digitToInt() } }

println(originalMap.toGraph().computeShortestPath())

fun List<Int>.incrementBy(n: Int) = map { if (it + n < 10) it + n else 1 + ((it + n) % 10) }
val replicatedRight = originalMap.map { line ->
    (0..4).map { n -> line.incrementBy(n) }.flatten()
}
val fullMap = (0..4).flatMap { n ->
    replicatedRight.map { it.incrementBy(n) }
}
println(fullMap.toGraph().computeShortestPath())
