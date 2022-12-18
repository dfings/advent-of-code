#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int, val z: Int)

class Pond(val lava: Set<Point>) {
    val airCache = mutableMapOf<Point, Boolean>()
    val xMin = lava.minOf { it.x }
    val xMax = lava.maxOf { it.x }
    val yMin = lava.minOf { it.y }
    val yMax = lava.maxOf { it.y }
    val zMin = lava.minOf { it.z }
    val zMax = lava.maxOf { it.z }

    fun totalSurfaceArea() = lava.sumOf { it.surfaceArea() }
    fun totalExteriorSurfaceAreal() = lava.sumOf { it.exteriorSurfaceArea() }

    fun Point.adjacentNonLava() = listOf(
        copy(x = x - 1),
        copy(x = x + 1),
        copy(y = y - 1),
        copy(y = y + 1),
        copy(z = z - 1),
        copy(z = z + 1)
    ).filter { it !in lava }

    fun Point.surfaceArea() = adjacentNonLava().count()
    fun Point.exteriorSurfaceArea() = adjacentNonLava().count { !it.isAir() }

    fun Point.isAir(): Boolean {
        airCache[this]?.let { return it }
        val frontier = ArrayDeque<Point>(listOf(this))
        val visited = mutableSetOf<Point>()
        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            if (!visited.add(current)) continue
            if (current in airCache) {
                val result = airCache.getValue(current)
                visited.forEach { airCache[it] = result }
                return result
            }
            if (current.outsideRange()) {
                visited.forEach { airCache[it] = false }
                return false
            }
            frontier.addAll(current.adjacentNonLava())
        }
        visited.forEach { airCache[it] = true }
        return true
    }

    fun Point.outsideRange() = x < xMin || x > xMax || y < yMin || y > yMax || z < zMin || z > zMax
}

val lines = java.io.File(args[0]).readLines()
val points = lines.map { it.split(",").map { it.toInt() } }.map { (x, y, z) -> Point(x, y, z) }
val pond = Pond(points.toSet())

println(pond.totalSurfaceArea())
println(pond.totalExteriorSurfaceAreal())
