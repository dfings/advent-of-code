#!/usr/bin/env kotlin

import kotlin.math.sqrt

data class Point(val index: Int, val x: Long, val y: Long, val z: Long)
data class Link(val a: Point, val b: Point, val distance: Double)

class UnionFind(val n: Int) {
    val parent = IntArray(n) { it }

    fun find(x: Int): Int {
        if (parent[x] != x) parent[x] = find(parent[x]) // Path Compression
        return parent[x]
    }

    fun union(x: Int, y: Int) {
        val rootX = find(x)
        val rootY = find(y)
        if (rootX != rootY) parent[rootX] = rootY
    }

    fun counts(): IntArray {
        val histogram = IntArray(n)
        for (i in 0..<n) {
            histogram[find(i)]++
        }
        return histogram
    }

    fun isSingle(): Boolean {
        val found = find(0)
        return (1..<n).all { found == find(it) }
    }
}

fun distance(a: Point, b: Point): Double =
    sqrt(((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z)).toDouble())

fun String.parse(index: Int): Point {
    val (x, y, z) = split(",").map { it.toLong() }
    return Point(index, x, y, z)
}

fun solve(lines: List<String>) {
    val points = lines.mapIndexed { i, it -> it.parse(i) }
    val links = points.flatMapIndexed { i, a -> points.mapIndexedNotNull { j, b -> 
        if (i < j) Link(a, b, distance(a, b)) else null 
    } }.sortedBy { it.distance }
    val unionFind = UnionFind(points.size)
    for (link in links.take(1000)) {
        unionFind.union(link.a.index, link.b.index)
    }
    println(unionFind.counts().sortedBy { -it }.take(3).reduce(Int::times))
    for (link in links.drop(1000)) {
        unionFind.union(link.a.index, link.b.index)
        if (unionFind.isSingle()) {
            println(link.a.x * link.b.x)
            break
        }
    }
}

solve(java.io.File(args[0]).readLines())
