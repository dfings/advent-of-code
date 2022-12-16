#!/usr/bin/env kotlin

data class Valve(val name: String, val flow: Int, val tunnels: List<String>)
data class Move(val valve: Valve, val length: Int)
data class Turn(val move: Move, val score: Int)
typealias TurnList = ArrayDeque<Turn>

class Graph(valves: List<Valve>) {
    private val valvesByName = valves.associateBy { it.name }

    val start = valvesByName.getValue("AA")
    val valvesByFlow = valves.sortedByDescending { it.flow }.filter { it.flow > 0 }

    fun getMoves(valve: Valve, remainingTime: Int): List<Move> {
        return moves.getValue(valve) + listOf(Move(start, remainingTime))
    }

    private val moves: Map<Valve, List<Move>> = buildMap {
        for (valve in valves.filter { it.flow > 0 || it == start }) {
            val others = valvesByFlow.filter { it != valve }
            put(valve, others.map { Move(it, 1 + findShortestPath(valve.name, it.name)!!) })
        }
    }

    private fun findShortestPath(source: String, target: String): Int? {
        val frontier = ArrayDeque<Pair<String, Int>>(listOf(source to 0))
        val seen = mutableSetOf<String>(source)
        while (!frontier.isEmpty()) {
            val (name, distance) = frontier.removeFirst()
            if (name == target) return distance
            valvesByName.getValue(name).tunnels.forEach {
                if (seen.add(it)) frontier.add(it to distance + 1)
            }
        }
        return null
    }
}

class Part1(graph: Graph) {
    val timeLimit = 30
    var minute = 0
    val remainingTime: Int get() = timeLimit - minute
    
    val turns = TurnList()
    val opened = HashSet<Valve>()
    var currentPosition: Valve = graph.start
    var currentScore = 0

    var bestTurns: TurnList? = null
    var bestScore = 0

    fun computeMaxFlow() {
        if (minute == timeLimit || opened.size == graph.valvesByFlow.size) {
            if (currentScore > bestScore) {
                bestScore = currentScore
                bestTurns = TurnList(turns)
            }
            return
        }

        // Detect can't win
        var maxScore = currentScore
        var index = 0
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * (remainingTime - index)
            index++
            if (minute + index >= timeLimit) break
        }
        if (maxScore < bestScore) return

        for (move in graph.getMoves(currentPosition, remainingTime)) {
            if (minute + move.length > timeLimit) continue
            if (move.valve != graph.start && !opened.add(move.valve)) continue
            minute += move.length
            val delta = (remainingTime) * move.valve.flow
            currentScore += delta
            turns.addLast(Turn(move, delta))
            currentPosition = move.valve
            computeMaxFlow()
            turns.removeLast()
            currentScore -= delta
            minute -= move.length
            if (move.valve != graph.start) opened.remove(move.valve)
        }
    }
}

class Part2(graph: Graph) {
    val timeLimit = 26
    var minute = 0
    val remainingTime: Int get() = timeLimit - minute
    var elephantMinute = 0
    val remainingElephantTime: Int get() = timeLimit - elephantMinute

    val turns = TurnList()
    val elephantTurns = TurnList()
    var currentPosition: Valve = graph.start
    var currentElephantPosition: Valve = graph.start
    var currentScore = 0
    val opened = HashSet<Valve>()

    var bestTurns: TurnList? = null
    var bestElephantTurns: TurnList? = null
    var bestScore = 0

    fun computeMaxFlow() {
        if (opened.size == graph.valvesByFlow.size) {
            maybeUpdateBestScore()
            return
        }

        if (minute == timeLimit) {
            computeMaxElephantFlow()
            return
        }

        // Detect can't win
        var maxScore = currentScore
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * timeLimit
        }
        if (maxScore < bestScore) return

        val lastPos = currentPosition
        for (move in graph.getMoves(currentPosition, remainingTime)) {
            if (minute + move.length > timeLimit) continue
            if (move.valve != graph.start && !opened.add(move.valve)) continue
            minute += move.length
            val delta = remainingTime * move.valve.flow
            currentScore += delta
            turns.addLast(Turn(move, delta))
            currentPosition = move.valve
            computeMaxFlow()
            currentPosition = lastPos
            turns.removeLast()
            currentScore -= delta
            minute -= move.length
            if (move.valve != graph.start) opened.remove(move.valve)
        }
    }

    fun computeMaxElephantFlow() {
        if (elephantMinute == timeLimit || opened.size == graph.valvesByFlow.size) {
            maybeUpdateBestScore()
            return
        }

        // Detect can't win
        var maxScore = currentScore
        var index = 0
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * (remainingElephantTime - index)
            index++
            if (elephantMinute + index >= timeLimit) break
        }
        if (maxScore < bestScore) return

        val lastPos = currentElephantPosition
        for (move in graph.getMoves(currentElephantPosition, remainingElephantTime)) {
            if (elephantMinute + move.length > timeLimit) continue
            if (move.valve != graph.start && !opened.add(move.valve)) continue
            elephantMinute += move.length
            val delta = remainingElephantTime * move.valve.flow
            currentScore += delta
            elephantTurns.addLast(Turn(move, delta))
            currentElephantPosition = move.valve
            computeMaxElephantFlow()
            currentElephantPosition = lastPos
            elephantTurns.removeLast()
            currentScore -= delta
            elephantMinute -= move.length
            if (move.valve != graph.start) opened.remove(move.valve)
        }
    }

    private fun maybeUpdateBestScore() {
        if (currentScore > bestScore) {
            bestScore = currentScore
            bestTurns = TurnList(turns)
            bestElephantTurns = TurnList(elephantTurns)
        }
    }
}

val pattern = Regex("Valve (..) has flow rate=(\\d+); .*valves? ([A-Z][A-Z].*)")
val lines = java.io.File(args[0]).readLines()
val valves = lines.map { pattern.find(it)!!.destructured }
    .map { (name, flow, tunnels) -> Valve(name, flow.toInt(), tunnels.split(", ")) }
val graph = Graph(valves)

val part1 = Part1(graph)
part1.computeMaxFlow()
part1.bestTurns?.forEachIndexed { i, it -> println("${i + 1}: $it") }
println(part1.bestScore)

val part2 = Part2(graph)
part2.computeMaxFlow()
part2.bestTurns?.forEachIndexed { i, it -> println("${i + 1}: $it") }
part2.bestElephantTurns?.forEachIndexed { i, it -> println("${i + 1}: $it") }
println(part2.bestScore)
