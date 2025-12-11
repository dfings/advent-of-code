#!/usr/bin/env kotlin

fun parse(lines: List<String>): Map<String, List<String>> = buildMap {
    for (line in lines) {
        val (from, to) = line.split(": ")
        put(from, to.split(" "))
    }
}

data class PathCount(val total: Long, val dac: Long, val fft: Long, val both: Long)

val pathCountCache = mutableMapOf<String, PathCount>()
fun Map<String, List<String>>.countPaths(from: String): PathCount = pathCountCache.getOrPut(from) {
    if (from == "out") return@getOrPut PathCount(1, 0, 0, 0)
    val out = getValue(from).map { countPaths(it) }.reduce { a, b -> 
        PathCount(a.total + b.total, a.dac + b.dac, a.fft + b.fft, a.both + b.both)
    }
    when (from) {
        "dac" -> out.copy(dac = out.total, both = out.fft)
        "fft" -> out.copy(fft = out.total, both = out.dac)
        else -> out
    }
}

fun solve(lines: List<String>) {
    val deviceMap = parse(lines)
    println(deviceMap.countPaths("you").total)
    println(deviceMap.countPaths("svr").both)
}

solve(java.io.File(args[0]).readLines())
