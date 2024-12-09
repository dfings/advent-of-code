#!/usr/bin/env kotlin

fun parse(diskMap: List<Int>): List<Int> {
    val filesystem = MutableList(diskMap.sum()) { -1 }
    var index = 0
    for (i in diskMap.indices) {
        for (j in 0..<diskMap[i]) {
            filesystem[index++] = if (i % 2 == 0) i / 2 else -1
        }
    }
    return filesystem
}

fun defrag(fIn: List<Int>): List<Int> {
    val fOut = fIn.toMutableList()
    var j = fOut.indexOfLast { it != -1 }
    for (i in fOut.indices) {
        when {
            i >= j -> break
            fOut[i] != -1 -> continue    
            else -> java.util.Collections.swap(fOut, i, j)
        }
        while (fOut[--j] == -1) {}
    }
    return fOut
}

fun List<Int>.findFreeSpace(needed: Int): Int {
    var start = 0
    var count = 0
    for (i in indices) {
        if (get(i) == -1) {
            if (count++ == 0) start = i
            if (count >= needed) return start
        } else {
            count = 0
        }
    }
    return -1
}

fun defrag2(fIn: List<Int>): List<Int> {
    val fOut = fIn.toMutableList()
    var j = fOut.indexOfLast { it != -1 }
    while (j > 0) {
        val jStart = fOut.subList(0, j).indexOfLast { it != fOut[j] } + 1
        val size = j - jStart + 1
        val freeSpaceIndex = fOut.findFreeSpace(size)
        if (freeSpaceIndex != -1 && j > freeSpaceIndex) {
            for (k in 0..<size) {
                java.util.Collections.swap(fOut, j - k, freeSpaceIndex + k)
            }
        }
        j = fOut.subList(0, jStart).indexOfLast { it != -1 }
    }
    return fOut
}

fun List<Int>.checksum() = mapIndexed { i, it -> if (it == -1) 0 else 1L * i * it }.sum()

val line = java.io.File(args[0]).readLines().single()
val diskMap = line.map { "$it".toInt() }
val filesystem = parse(diskMap)

println(defrag(filesystem).checksum())
println(defrag2(filesystem).checksum())
