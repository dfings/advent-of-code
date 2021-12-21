#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val initialPositions = lines.map { it.split(": ").mapNotNull { it.toIntOrNull() }.single() }

data class GameState(val positions: Pair<Int, Int>, val scores: Pair<Int, Int> = 0 to 0) {
    fun isActive(cutoff: Int) = scores.first < cutoff && scores.second < cutoff

    fun advance(player: Int, roll: Int) =
        if (player == 1) {
            val p = nextPosition(positions.first, roll)
            val s = scores.first + p
            GameState(positions.copy(first = p), scores.copy(first = s))
        } else {
            val p = nextPosition(positions.second, roll)
            val s = scores.second + p
            GameState(positions.copy(second = p), scores.copy(second = s))
        }

    fun nextPosition(current: Int, move: Int) = (current + move - 1) % 10 + 1
}

// Part 1

fun Iterator<Int>.roll() = (0..2).sumOf { next() }
val dice = generateSequence(1) { 1 + (it % 100) }.iterator()
var gameState = GameState(initialPositions[0] to initialPositions[1])
var turnCount = 0
while (gameState.isActive(1000)) {
    turnCount++
    gameState = gameState.advance(1, dice.roll())
    if (!gameState.isActive(1000)) break
    turnCount++
    gameState = gameState.advance(2, dice.roll())
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

var stateCounts = mapOf(GameState(initialPositions[0] to initialPositions[1]) to 1L)
while (stateCounts.keys.any { it.isActive(21) }) {
    stateCounts = advanceDiracState(stateCounts, 1)
    stateCounts = advanceDiracState(stateCounts, 2)
}
val player1Wins = stateCounts.entries.filter { it.key.scores.first >= 21 }.sumOf { it.value }
val player2Wins = stateCounts.entries.filter { it.key.scores.second >= 21 }.sumOf { it.value }
println(listOf(player1Wins, player2Wins).maxOf { it })
