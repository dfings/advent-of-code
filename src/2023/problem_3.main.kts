#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun Char.isSymbol() = !isDigit() && this != '.'

fun List<String>.charAt(x: Int, y: Int) = when {
    y < 0 || y >= size -> '.'
    x < 0 || x >= get(y).length -> '.'
    else -> get(y).get(x)
}

fun List<String>.isAdjacentToSymbol(x: Int, y: Int): Boolean {
    for (i in -1..1) {
        for (j in -1..1) {
            if (charAt(x + i, y + j).isSymbol()) {
                return true
            }
        }
    }
    if (charAt(x + 1, y).isDigit()) {
        return isAdjacentToSymbol(x + 1, y)
    }
    return false
}

var sum = 0
for (y in 0..lines.lastIndex) {
    var x = 0
    while (x < lines[y].lastIndex) {
        if (lines.charAt(x, y).isDigit() && lines.isAdjacentToSymbol(x, y)) {
            val numberString = lines[y].substring(x).takeWhile { it.isDigit() }
            x += numberString.length
            sum += numberString.toInt()
        } else {
            x += 1
        }
    }
}
println(sum)
