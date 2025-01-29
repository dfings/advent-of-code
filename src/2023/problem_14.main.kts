#!/usr/bin/env kotlin

fun part1(lines: List<String>): Int {
    var load = 0
    for (x in lines[0].indices) {
        var rock = -1
        for (y in lines.indices) {
            when (lines[y][x]) {
                '#' -> rock = y
                'O' -> {
                    rock++
                    load += lines.size - rock
                }
            }
        }
    }
    return load
}

fun solve(lines: List<String>) {
    println(part1(lines))
}

solve(java.io.File(args[0]).readLines())
