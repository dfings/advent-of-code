#!/usr/bin/env kotlin

fun List<List<Char>>.isPaper(x: Int, y: Int): Boolean =
    y in indices && x in get(y).indices && this[y][x] == '@'

fun List<List<Char>>.countPaper(x: Int, y: Int): Int =
    (-1..1).sumOf{ j -> (-1..1).sumOf { i -> if (isPaper(x + i, y + j)) 1 else 0 } }

fun MutableList<MutableList<Char>>.maybeRemove(remove: Boolean): Int {
    var count = 0
    for (y in indices) {
        for (x in get(y).indices) {
            if (this[y][x] == '@' && countPaper(x, y) < 5) {
                if (remove) this[y][x] = '.'
                count++
            }
        }
    }
    return count
}

fun solve(input: List<String>) {
    val graph = input.map { it.toMutableList() }.toMutableList()
    println(graph.maybeRemove(false))
    var count = 0
    while (true) {
        count += graph.maybeRemove(true).also { if (it == 0) break }
    }
    println(count)
}

solve(java.io.File(args[0]).readLines())
