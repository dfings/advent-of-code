#!/usr/bin/env kotlin

data class Point(val x: Int, val y: Int, val z: Int)

class Pond(val lava: Set<Point>) {
    val airCache = mutableMapOf<Point, Boolean>()
    val pMin = Point(lava.minOf { it.x }, lava.minOf { it.y }, lava.minOf { it.z })
    val pMax = Point(lava.maxOf { it.x }, lava.maxOf { it.y }, lava.maxOf { it.z })

    fun totalSurfaceArea() = lava.sumOf { it.adjacentNonLava().count() }
    fun totalExteriorSurfaceAreal() = lava.sumOf { it.adjacentNonLava().count { !it.isAir() } }

    fun Point.adjacentNonLava() = sequenceOf(
        copy(x = x - 1),
        copy(x = x + 1),
        copy(y = y - 1),
        copy(y = y + 1),
        copy(z = z - 1),
        copy(z = z + 1)
    ).filter { it !in lava }

    fun Point.isAir(): Boolean {
        airCache[this]?.let { return it }
        val frontier = ArrayDeque<Point>(listOf(this))
        val visited = mutableSetOf<Point>()
        while (!frontier.isEmpty()) {
            val current = frontier.removeFirst()
            when {
                !visited.add(current) -> continue
                current in airCache -> return addToCache(visited, airCache.getValue(current))
                current.outsideRange() -> return addToCache(visited, false)
                else -> frontier.addAll(current.adjacentNonLava())
            }
        }
        return addToCache(visited, true)
    }

    fun addToCache(points: Iterable<Point>, isAir: Boolean): Boolean {
        points.forEach { airCache[it] = isAir }
        return isAir
    }

    fun Point.outsideRange() = x < pMin.x || x > pMax.x || y < pMin.y || y > pMax.y || z < pMin.z || z > pMax.z
}

val lines = java.io.File(args[0]).readLines()
val points = lines.map { it.split(",").map { it.toInt() } }.map { (x, y, z) -> Point(x, y, z) }
val pond = Pond(points.toSet())

println(pond.totalSurfaceArea())
println(pond.totalExteriorSurfaceAreal())
