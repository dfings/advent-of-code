#!/usr/bin/env kotlin

import kotlin.math.max

data class Valve(val name: String, val flow: Int, val tunnels: List<String>)
data class Move(val valve: Valve, val length: Int)

class Graph(valves: List<Valve>) {
    private val valvesByName = valves.associateBy { it.name }

    val start = valvesByName.getValue("AA")
    val valvesByFlow = valves.sortedByDescending { it.flow }.filter { it.flow > 0 }
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

        for (move in graph.moves.getValue(currentPosition)) {
            recursivelyMove(move.valve, move.length)
        }
        recursivelyMove(graph.start, remainingTime)
    }

    private fun recursivelyMove(valve: Valve, length: Int) {
        if (minute + length > timeLimit) return
        if (valve != graph.start && !opened.add(valve)) return
        minute += length
        val delta = (remainingTime) * valve.flow
        currentScore += delta
        val lastPosition = currentPosition
        currentPosition = valve
        computeMaxFlow()
        currentPosition = lastPosition
        currentScore -= delta
        minute -= length
        if (valve != graph.start) opened.remove(valve)
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
        recursivelyTryToMove()
    }

    private fun recursivelyTryToMove() {
        if (opened.size == graph.valvesByFlow.size) {
            bestScore = max(bestScore, currentScore)
            return
        }

        if (minute == timeLimit) {
            recursivelyTryToMoveElephant()
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
            recursivelyMove(move.valve, move.length)
        }
        recursivelyMove(graph.start, remainingTime)
    }

    private fun recursivelyMove(valve: Valve, length: Int) {
        if (minute + length > timeLimit) return
        if (!tryOpen(valve)) return
        minute += length
        val delta = remainingTime * valve.flow
        currentScore += delta
        val lastPosition = currentPosition
        currentPosition = valve
        recursivelyTryToMove()
        currentPosition = lastPosition
        currentScore -= delta
        minute -= length
        tryClose(valve)
    }

    private fun recursivelyTryToMoveElephant() {
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
            recursivelyMoveElephant(move.valve, move.length)
        }
        recursivelyMoveElephant(graph.start, remainingElephantTime)
    }

    private fun recursivelyMoveElephant(valve: Valve, length: Int) {
        if (elephantMinute + length > timeLimit) return
        if (!tryOpen(valve)) return
        elephantMinute += length
        val delta = remainingElephantTime * valve.flow
        currentScore += delta
        val lastElephantPosition = currentElephantPosition
        currentElephantPosition = valve
        recursivelyTryToMoveElephant()
        currentElephantPosition = lastElephantPosition
        currentScore -= delta
        elephantMinute -= length
        tryClose(valve)
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
