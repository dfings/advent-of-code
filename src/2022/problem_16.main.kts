#!/usr/bin/env kotlin

import kotlin.math.max

data class Valve(val name: String, val flow: Int, val tunnels: List<String>)
data class Move(val valve: Valve, val length: Int)
data class Turn(val move: Move, val score: Int)

class Graph(valves: List<Valve>) {
    private val valvesByName = valves.associateBy { it.name }

    val start = valvesByName.getValue("AA")
    val valvesByFlow = valves.sortedByDescending { it.flow }.filter { it.flow > 0 }

    fun getMoves(valve: Valve, remainingTime: Int): List<Move> {
        return moves.getValue(valve) + listOf(Move(start, remainingTime))
    }

    val moves: Map<Valve, List<Move>> = buildMap {
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

    val opened = HashSet<Valve>()
    var currentPosition: Valve = graph.start
    var currentScore = 0

    var bestScore = 0

    fun computeMaxFlow() {
        if (minute == timeLimit || opened.size == graph.valvesByFlow.size) {
            bestScore = max(bestScore, currentScore)
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
            currentPosition = move.valve
            computeMaxFlow()
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

    var currentPosition: Valve = graph.start
    var currentElephantPosition: Valve = graph.start
    var currentScore = 0
    val opened = HashSet<Valve>()

    var bestScore = 0

    fun computeMaxFlow() {
        makeNextMove()
    }

    private fun makeNextMove() {
        if (opened.size == graph.valvesByFlow.size) {
            bestScore = max(bestScore, currentScore)
            return
        }

        if (minute == timeLimit) {
            makeNextElephantMove()
            return
        }

        // Detect can't win
        var maxScore = currentScore
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * timeLimit
        }
        if (maxScore < bestScore) return

        for (move in graph.moves.getValue(currentPosition)) {
            recursivelyMove(move)
        }
        recursivelyMove(Move(graph.start, remainingTime))
    }

    private fun recursivelyMove(move: Move) {
        if (minute + move.length > timeLimit) return
        if (!tryOpen(move.valve)) return
        minute += move.length
        val delta = remainingTime * move.valve.flow
        currentScore += delta
        val lastPosition = currentPosition
        currentPosition = move.valve
        makeNextMove()
        currentPosition = lastPosition
        currentScore -= delta
        minute -= move.length
        tryClose(move.valve)
    }

    private fun makeNextElephantMove() {
        if (elephantMinute == timeLimit || opened.size == graph.valvesByFlow.size) {
            bestScore = max(bestScore, currentScore)
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

        for (move in graph.moves.getValue(currentElephantPosition)) {
            recursivelyMoveElephant(move)
        }
        recursivelyMoveElephant(Move(graph.start, remainingElephantTime))
    }

    private fun recursivelyMoveElephant(move: Move) {
        if (elephantMinute + move.length > timeLimit) return
        if (!tryOpen(move.valve)) return
        elephantMinute += move.length
        val delta = remainingElephantTime * move.valve.flow
        currentScore += delta
        val lastElephantPosition = currentElephantPosition
        currentElephantPosition = move.valve
        makeNextElephantMove()
        currentElephantPosition = lastElephantPosition
        currentScore -= delta
        elephantMinute -= move.length
        tryClose(move.valve)
    }

    private fun tryOpen(valve: Valve) = valve == graph.start || opened.add(valve)
    private fun tryClose(valve: Valve) {
        if (valve != graph.start) opened.remove(valve)
    }
}

val pattern = Regex("Valve (..) has flow rate=(\\d+); .*valves? ([A-Z][A-Z].*)")
val lines = java.io.File(args[0]).readLines()
val valves = lines.map { pattern.find(it)!!.destructured }
    .map { (name, flow, tunnels) -> Valve(name, flow.toInt(), tunnels.split(", ")) }
val graph = Graph(valves)

val part1 = Part1(graph)
part1.computeMaxFlow()
println(part1.bestScore)

val part2 = Part2(graph)
part2.computeMaxFlow()
println(part2.bestScore)
