#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val initialPositions = lines.map { Player(it.split(": ").mapNotNull { it.toIntOrNull() }.single()) }

data class Player(val position: Int, val score: Int = 0) {
    fun takeTurn(roll: Int) = nextPosition(position, roll).let { Player(it, score + it) }
    fun nextPosition(current: Int, move: Int) = (current + move - 1) % 10 + 1
}

data class GameState(val players: List<Player>, val activePlayer: Int = 0) {
    val lastActivePlayer: Int get() = 1 - activePlayer
    fun isGameOver(cutoff: Int) = players.any { it.score >= cutoff }
    fun takeTurn(roll: Int): GameState {
        val newPlayers = players.toMutableList()
        newPlayers[activePlayer] = players[activePlayer].takeTurn(roll)
        return GameState(newPlayers, lastActivePlayer)
    }
}

// Part 1
val dice = generateSequence(1) { 1 + (it % 100) }.iterator()
var gameState = GameState(initialPositions)
var turnCount = 0
while (!gameState.isGameOver(1000)) {
    turnCount++
    gameState = gameState.takeTurn(dice.next() + dice.next() + dice.next())
}
println(gameState.players.minOf { it.score * (turnCount * 3) })

// Part 2
fun takeTurnDiracState(start: Map<GameState, Long>): Map<GameState, Long> = buildMap {
    start.entries.filter { it.key.isGameOver(21) }.forEach { put(it.key, it.value) }
    for ((startGameState, count) in start.entries.filter { !it.key.isGameOver(21) }) {
        for (roll1 in 1..3) for (roll2 in 1..3) for (roll3 in 1..3) {
            val roll = roll1 + roll2 + roll3
            val nextState = startGameState.takeTurn(roll)
            put(nextState, count + (get(nextState) ?: 0L))            
        }
    }
}

var stateCounts = mapOf(GameState(initialPositions) to 1L)
while (!stateCounts.keys.all { it.isGameOver(21) }) {
    stateCounts = takeTurnDiracState(stateCounts)
}
println(stateCounts.entries.partition { it.key.players[0].score >= 21 }.toList().map { it.sumOf { it.value } }.maxOf { it })

// Alternate part 2
val cachedWinCounts = mutableMapOf<GameState, List<Long>>()
fun getWinCount(gameState: GameState): List<Long> = cachedWinCounts.getOrPut(gameState) {
    val winCount = mutableListOf(0L, 0L)
    for (roll1 in 1..3) for (roll2 in 1..3) for (roll3 in 1..3) {
        val roll = roll1 + roll2 + roll3
        val nextState = gameState.takeTurn(roll)
        if (nextState.isGameOver(21)) {
            winCount[gameState.lastActivePlayer] += 1L
        } else {
            val nextStateWinCounts = getWinCount(nextState)
            winCount[0] += nextStateWinCounts[0]
            winCount[1] += nextStateWinCounts[1]
        }
    }
    winCount
}
println(getWinCount(GameState(initialPositions)).maxOf { it })
