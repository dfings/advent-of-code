#!/usr/bin/env kotlin

import java.io.File

class Square(val number: Int, var marked: Boolean = false) {
    fun observe(pick: Int) { 
        if (pick == number) marked = true 
    }
}

class Row(val squares: List<Square>) {
    val bingo: Boolean get() = squares.all { it.marked }
    val score: Int get() = squares.sumOf { if (it.marked) 0 else it.number }
    fun observe(pick: Int) = squares.forEach { it.observe(pick) }
}

class Board(val rows: List<Row>) {
    var winningPick = 0
    // Treat columns as transposed rows.
    val cols: List<Row> = rows.indices.map { i -> Row(rows.map { it.squares[i] }) }   
    val bingo: Boolean get() = rows.any { it.bingo } || cols.any { it.bingo }
    val score: Int get() = winningPick * rows.sumOf { it.score }
    fun observe(pick: Int) = rows.forEach { it.observe(pick) }
}

val whitespace = Regex("\\s+")
fun parseSquare(num: String) = Square(num.toInt())
fun parseRow(line: String) = Row(line.trim().split(whitespace).map(::parseSquare))
fun parseBoard(lines: List<String>) = Board(lines.map(::parseRow))

val lines = File(args[0]).readLines()
val picks = lines.first().split(",").map { it.toInt() }
val boardLines = lines.drop(2)
val boardSize = boardLines.takeWhile { it != "" }.size
val boards = boardLines.filter { it != "" }.chunked(boardSize).map(::parseBoard)

val players = boards.toMutableSet()
val winners = mutableListOf<Board>()
for (pick in picks) {
    players.forEach { it.observe(pick) }
    val bingo = players.filter { it.bingo }
    bingo.forEach { it.winningPick = pick }
    players.removeAll(bingo)
    winners.addAll(bingo)
}

println(winners.first().score)
println(winners.last().score)
