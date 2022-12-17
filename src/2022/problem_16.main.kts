#!/usr/bin/env kotlin

import kotlin.math.max

data class Valve(val name: String, val flow: Int, val tunnels: List<String>)
data class Move(val valve: Valve, val length: Int)

class Graph(valves: List<Valve>) {
    private val valvesByName = valves.associateBy { it.name }

    val start = valvesByName.getValue("AA")
    val valvesByFlow = valves.sortedByDescending { it.flow }.filter { it.flow > 0 }
    val valveCount = valvesByFlow.size
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

class Agent(val start: Valve, val timeLimit: Int) {
    var minute = 0
    val remainingTime: Int get() = timeLimit - minute
    var currentPosition: Valve = start
}

class Solver(graph: Graph, val timeLimit: Int, val agentCount: Int) {
    val stopMoving = Valve("END", 0, listOf()) // Dummy value that signals no more moves

    val agents = List<Agent>(agentCount) { Agent(graph.start, timeLimit) }
    var agentIndex = 0
    val agent: Agent get() = agents[agentIndex]

    val opened = HashSet<Valve>()
    var currentScore = 0
    var bestScore = 0

    fun computeMaxFlow() {
        recursivelyTryToMove()
    }

    private fun recursivelyTryToMove() {
        if (opened.size == graph.valveCount) {
            bestScore = max(bestScore, currentScore)
            return
        }

        if (agent.minute == timeLimit) {
            if (agentIndex == agents.lastIndex) {
                bestScore = max(bestScore, currentScore)
            } else {
                agentIndex++
                recursivelyTryToMove()
                agentIndex--
            }
            return
        }

        if (cannotWin()) return

        for (move in graph.moves.getValue(agent.currentPosition)) {
            recursivelyTryToMoveTo(move.valve, move.length)
        }
        recursivelyTryToMoveTo(stopMoving, agent.remainingTime)
    }

    private fun cannotWin(): Boolean {
        var maxScore = currentScore
        if (agentIndex < agents.lastIndex) {
            for (i in graph.valvesByFlow.indices) {
                if (graph.valvesByFlow[i] in opened) continue
                maxScore += graph.valvesByFlow[i].flow * timeLimit
            }
        } else {
            var index = 0
            for (i in graph.valvesByFlow.indices) {
                if (graph.valvesByFlow[i] in opened) continue
                maxScore += graph.valvesByFlow[i].flow * (agent.remainingTime - index)
                index++
                if (agent.minute + index >= timeLimit) break
            }
        }
        return maxScore < bestScore
    }

    private fun recursivelyTryToMoveTo(valve: Valve, length: Int) {
        if (agent.minute + length > timeLimit) return

        if (valve != stopMoving && !opened.add(valve)) return
        agent.minute += length
        val delta = agent.remainingTime * valve.flow
        currentScore += delta
        val lastPosition = agent.currentPosition
        agent.currentPosition = valve

        recursivelyTryToMove()

        agent.currentPosition = lastPosition
        currentScore -= delta
        agent.minute -= length
        if (valve != stopMoving) opened.remove(valve)
    }
}

val pattern = Regex("Valve (..) has flow rate=(\\d+); .*valves? ([A-Z][A-Z].*)")
val lines = java.io.File(args[0]).readLines()
val valves = lines.map { pattern.find(it)!!.destructured }
    .map { (name, flow, tunnels) -> Valve(name, flow.toInt(), tunnels.split(", ")) }
val graph = Graph(valves)

val part1 = Solver(graph, 30, 1)
part1.computeMaxFlow()
println(part1.bestScore)

val part2 = Solver(graph, 26, 2)
part2.computeMaxFlow()
println(part2.bestScore)
