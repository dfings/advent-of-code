#!/usr/bin/env kotlin

data class State(val value: Long, val row: Int, val column: Int)
fun State.next() = State(
    value = (value * 252533) % 33554393,
    row = if (row == 1) column + 1 else row - 1,
    column = if (row == 1) 1 else column + 1,
)

val input = java.io.File(args[0]).readLines().single()
val (row, column) = Regex("""row (\d+).*column (\d+)""").find(input)!!.destructured
val targetRow = row.toInt()
val targetColumn = column.toInt()

val states = generateSequence(State(20151125, 1, 1), State::next)
println(states.find { it.row == targetRow && it.column == targetColumn})
