#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val initialPositions = lines.map { it.split(": ").mapNotNull { it.toIntOrNull() }.single() }

data class GameState(val positions: List<Int>, val scores: List<Int> = listOf(0, 0)) {
    fun isActive(cutoff: Int) = scores.all { it < cutoff }

    fun advance(player: Int, roll: Int): GameState {
        val newPositions = positions.toMutableList()
        newPositions[player] = nextPosition(positions[player], roll)
        val newScores = scores.toMutableList()
        newScores[player] = scores[player] + newPositions[player]
        return GameState(newPositions, newScores)
    }

    fun nextPosition(current: Int, move: Int) = (current + move - 1) % 10 + 1
}

// Part 1

fun Iterator<Int>.roll() = next() + next() + next()
val dice = generateSequence(1) { 1 + (it % 100) }.iterator()
var gameState = GameState(initialPositions)
var turnCount = 0
while (gameState.isActive(1000)) {
    gameState = gameState.advance(turnCount++ % 2, dice.roll())
}
println(gameState.scores.toList().minOf { it} * (turnCount * 3))

// Part 2
fun advanceDiracState(start: Map<GameState, Long>, player: Int): Map<GameState, Long> = buildMap {
    start.entries.filter { !it.key.isActive(21) }.forEach { put(it.key, it.value) }
    for ((startGameState, count) in start.entries.filter { it.key.isActive(21) }) {
        for (roll1 in 1..3) for (roll2 in 1..3) for (roll3 in 1..3) {
            val roll = roll1 + roll2 + roll3
            val nextState = startGameState.advance(player, roll)
            put(nextState, count + (get(nextState) ?: 0L))            
        }
    }
}

var stateCounts = mapOf(GameState(initialPositions) to 1L)
while (stateCounts.keys.any { it.isActive(21) }) {
    stateCounts = advanceDiracState(stateCounts, 0)
    stateCounts = advanceDiracState(stateCounts, 1)
}
val player1Wins = stateCounts.entries.filter { it.key.scores[0] >= 21 }.sumOf { it.value }
val player2Wins = stateCounts.entries.filter { it.key.scores[1] >= 21 }.sumOf { it.value }
println(listOf(player1Wins, player2Wins).maxOf { it })
