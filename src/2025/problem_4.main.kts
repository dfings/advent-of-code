#!/usr/bin/env kotlin

fun List<List<Char>>.isPaper(x: Int, y: Int): Boolean {
    if (y !in indices || x !in get(y).indices) return false
    return get(y).get(x) == '@'
}

fun List<List<Char>>.countAdjacent(x: Int, y: Int): Int {
    var count = 0
    for (j in -1..1) {
        for (i in -1..1) {
            if ((j != 0 || i != 0 )&& isPaper(x + i, y + j)) count++
        }
    }
    return count
}

fun MutableList<MutableList<Char>>.canRemove(remove: Boolean): Int {
    var count = 0
    for (y in indices) {
        for (x in get(y).indices) {
            if (this[y][x] == '@' && countAdjacent(x, y) < 4) {
                if (remove) this[y][x] = '.'
                count++
            }
        }
    }
    return count
}

fun solve(input: List<String>) {
    val graph = input.map { it.toMutableList() }.toMutableList()
    println(graph.canRemove(false))
    var count = 0
    while (true) {
        val x = graph.canRemove(true)
        if (x == 0) break
        count += x
    }
    println(count)
}

solve(java.io.File(args[0]).readLines())
