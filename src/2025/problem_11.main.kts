#!/usr/bin/env kotlin

fun parse(lines: List<String>): Map<String, List<String>> = buildMap {
    for (line in lines) {
        val (from, to) = line.split(": ")
        put(from, to.split(" "))
    }
}

val pathCountCache = mutableMapOf<String, Int>()
fun Map<String, List<String>>.countPaths(from: String): Int = pathCountCache.getOrPut(from) {
    if (from == "out") 1 else getValue(from).sumOf { countPaths(it) }
}

fun solve(lines: List<String>) {
    val deviceMap = parse(lines)
    println(deviceMap.countPaths("you"))
}

solve(java.io.File(args[0]).readLines())
