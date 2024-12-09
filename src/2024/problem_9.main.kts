#!/usr/bin/env kotlin

fun parse(diskMap: List<Int>): List<Int> {
    val filesystem = MutableList(diskMap.sum()) { -1 }
    var block = 0
    for (i in diskMap.indices) {
        repeat (diskMap[i]) {
            filesystem[block++] = if (i % 2 == 0) i / 2 else -1
        }
    }
    return filesystem
}

fun defrag(fIn: List<Int>): List<Int> {
    val fOut = fIn.toMutableList()
    var j = fOut.indexOfLast { it != -1 }
    for (i in fOut.indices) {
        if (i >= j) break
        if (fOut[i] == -1) {
            java.util.Collections.swap(fOut, i, j)
            while (fOut[--j] == -1) {}
        }
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
    var fileEnd = fOut.indexOfLast { it != -1 }
    while (fileEnd > 0) {
        val fileStart = fOut.subList(0, fileEnd).indexOfLast { it != fOut[fileEnd] } + 1
        val fileSize = fileEnd - fileStart + 1
        val freeSpaceIndex = fOut.findFreeSpace(fileSize)
        if (freeSpaceIndex != -1 && fileEnd > freeSpaceIndex) {
            for (k in 0 until fileSize) {
                java.util.Collections.swap(fOut, fileStart + k, freeSpaceIndex + k)
            }
        }
        fileEnd = fOut.subList(0, fileStart).indexOfLast { it != -1 }
    }
    return fOut
}

fun List<Int>.checksum() = mapIndexed { i, it -> if (it == -1) 0 else 1L * i * it }.sum()

val line = java.io.File(args[0]).readLines().single()
val diskMap = line.map { "$it".toInt() }
val filesystem = parse(diskMap)

println(defrag(filesystem).checksum())
println(defrag2(filesystem).checksum())
