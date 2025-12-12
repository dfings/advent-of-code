#!/usr/bin/env kotlin

data class Area(val length: Int, val width: Int, val boxes: List<Int>)

fun String.parseArea(): Area {
    val (lw, boxes) = split(": ")
    val (l, w) = lw.split("x")
    return Area(l.toInt(), w.toInt(), boxes.split(" ").map { it.toInt() })
}

fun solve(lines: List<String>) {
    val shapes = lines.takeWhile { "x" !in it }.chunked(5).map { it.joinToString().count { it == '#' } }
    val areas = lines.takeLastWhile { it.isNotEmpty() }.map { it.parseArea() }
    println(areas.count { area ->
        area.length * area.width >= area.boxes.mapIndexed { i, it -> it * shapes[i] }.sum()
    })
}

solve(java.io.File(args[0]).readLines())
