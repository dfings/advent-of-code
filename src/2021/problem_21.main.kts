#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val input = lines.map { it.split(": ").mapNotNull { it.toIntOrNull() }.single() }

// Part 1
fun nextPosition(current: Int, move: Int) = (current + move - 1) % 10 + 1

fun Iterator<Int>.roll() = (0..2).sumOf { next() }
val positions = input.toMutableList()
val scores = mutableListOf(0, 0)
val dice = generateSequence(1) { 1 + (it % 100) }.iterator()
var turnCount = 0

while (true) {
    turnCount++
    val roll0 = dice.roll()
    positions[0] = nextPosition(positions[0], roll0)
    scores[0] += positions[0] 
    if (scores[0] >= 1000) break

    turnCount++
    val roll1 = dice.roll()
    positions[1] = nextPosition(positions[1], roll1)
    scores[1] += positions[1]
    if (scores[1] >= 1000) break
}

println(scores.minOf { it} * (turnCount * 3))

// Part 2
data class GameState(val positions: Pair<Int, Int>, val scores: Pair<Int, Int>, val active: Boolean = true) {
    fun advance(player: Int, roll: Int) =
        if (player == 1) {
            val p = nextPosition(positions.first, roll)
            val s = scores.first + p
            GameState(positions.copy(first = p), scores.copy(first = s), s < 21)
        } else {
            val p = nextPosition(positions.second, roll)
            val s = scores.second + p
            GameState(positions.copy(second = p), scores.copy(second = s), s < 21)
        }
}

fun advanceState(start: Map<GameState, Long>, player: Int): Map<GameState, Long> = buildMap {
    start.entries.filter { !it.key.active }.forEach { put(it.key, it.value) }
    for ((startGameState, count) in start.entries.filter { it.key.active }) {
        for (roll1 in 1..3) for (roll2 in 1..3) for (roll3 in 1..3) {
            val roll = roll1 + roll2 + roll3
            val nextState = startGameState.advance(player, roll)
            put(nextState, count + (get(nextState) ?: 0L))            
        }
    }
}

var stateCounts = mapOf(GameState(input[0] to input[1], 0 to 0) to 1L)
while (stateCounts.keys.any { it.active }) {
    stateCounts = advanceState(stateCounts, 1)
    stateCounts = advanceState(stateCounts, 2)
}
val player1Wins = stateCounts.entries.filter { it.key.scores.first >= 21 }.sumOf { it.value }
val player2Wins = stateCounts.entries.filter { it.key.scores.second >= 21 }.sumOf { it.value }
println(listOf(player1Wins, player2Wins).maxOf { it })
