#!/usr/bin/env kotlin

import kotlin.math.min

enum class Direction { HORIZONTAL, VERTICAL }
data class Reflection(val direction: Direction, val start: Int)
fun Reflection.score() = if (direction == Direction.VERTICAL) start + 1 else 100 * (start + 1)

fun isVerticalReflection(map: List<String>, start: Int, target: Int): Boolean {
    var errors = 0
    for (y in map.indices) {
        for (i in 0..min(start, map[y].lastIndex - start - 1)) {
            if (map[y][start - i] != map[y][start + 1 + i]) errors++
        }
    }
    return errors == target
}

fun isHorizontalReflection(map: List<String>, start: Int, target: Int): Boolean {
    var errors = 0
    for (x in map[0].indices) {
        for (i in 0..min(start, map.lastIndex - start - 1)) {
            if (map[start - i][x] != map[start + 1 + i][x]) errors++
        }
    }
    return errors == target
}

fun findReflection(map: List<String>, target: Int): Reflection {
    val x = (0..map[0].lastIndex - 1).singleOrNull { isVerticalReflection(map, it, target) }
    val y = (0..map.lastIndex - 1).singleOrNull { isHorizontalReflection(map, it, target) }
    return when {
        x != null -> return Reflection(Direction.VERTICAL, x)
        y != null -> return Reflection(Direction.HORIZONTAL, y)
        else -> throw IllegalStateException()
    }
}

fun solve(lines: List<String>) {
    val mapCount = lines.count { it.isEmpty() }
    val maps = List(mapCount + 1) { mutableListOf<String>() }
    var i = 0
    for (line in lines) {
        if (line.isBlank()) i++ else maps[i] += line
    }
    println(maps.map { findReflection(it, 0) }.sumOf { it.score() })
    println(maps.map { findReflection(it, 1) }.sumOf { it.score() })
}

solve(java.io.File(args[0]).readLines())
