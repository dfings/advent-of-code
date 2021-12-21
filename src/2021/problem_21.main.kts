#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val initialPositions = lines.map { Player(it.split(": ").mapNotNull { it.toIntOrNull() }.single()) }

data class Player(val position: Int, val score: Int = 0) {
    fun advance(roll: Int) = nextPosition(position, roll).let { Player(it, score + it) }
    fun nextPosition(current: Int, move: Int) = (current + move - 1) % 10 + 1
}

data class GameState(val players: List<Player>) {
    fun isActive(cutoff: Int) = players.all { it.score < cutoff }
    fun advance(player: Int, roll: Int): GameState {
        val newPlayers = players.toMutableList()
        newPlayers[player] = players[player].advance(roll)
        return GameState(newPlayers)
    }
}

// Part 1
val dice = generateSequence(1) { 1 + (it % 100) }.iterator()
var gameState = GameState(initialPositions)
var turnCount = 0
while (gameState.isActive(1000)) {
    gameState = gameState.advance(turnCount++ % 2, dice.next() + dice.next() + dice.next())
}
println(gameState.players.minOf { it.score * (turnCount * 3) })

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
println(stateCounts.entries.partition { it.key.players[0].score >= 21 }.toList().map { it.sumOf { it.value } }.maxOf { it })

// Alternate part 2
val cachedWinCounts = mutableMapOf<Pair<GameState, Int>, List<Long>>()
fun getWinCount(gameState: GameState, player: Int): List<Long> {
    val cached = cachedWinCounts.get(gameState to player)
    if (cached != null) return cached

    val winCount = mutableListOf(0L, 0L)
    for (roll1 in 1..3) for (roll2 in 1..3) for (roll3 in 1..3) {
        val roll = roll1 + roll2 + roll3
        val nextState = gameState.advance(player, roll)
        if (nextState.players[player].score >= 21) {
            winCount[player] += 1L
        } else {
            val nextStateWinCounts = getWinCount(nextState, 1 - player)
            winCount[0] += nextStateWinCounts[0]
            winCount[1] += nextStateWinCounts[1]
        }
    }

    cachedWinCounts[gameState to player] = winCount
    return winCount
}
println(getWinCount(GameState(initialPositions), 0).maxOf { it })
