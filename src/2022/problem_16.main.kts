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

class AgentState(val start: Valve, val timeLimit: Int) {
    var minute = 0
    val remainingTime: Int get() = timeLimit - minute
    var currentPosition: Valve = start
}

class Solver(graph: Graph, val timeLimit: Int, val agentCount: Int) {
    val agents = List<AgentState>(agentCount) { AgentState(graph.start, timeLimit) }

    val opened = HashSet<Valve>()
    var currentScore = 0
    var bestScore = 0

    fun computeMaxFlow() {
        recursivelyTryToMove(0)
    }

    private fun recursivelyTryToMove(agent: Int) {
        val state = agents[agent]
        if (opened.size == graph.valveCount) {
            bestScore = max(bestScore, currentScore)
            return
        }

        if (state.minute == timeLimit) {
            if (agent == agents.lastIndex) {
                bestScore = max(bestScore, currentScore)
            } else {
                recursivelyTryToMove(agent + 1)
            }
            return
        }

        if (cannotWin(agent)) return

        for (move in graph.moves.getValue(state.currentPosition)) {
            recursivelyTryToMoveTo(agent, move.valve, move.length)
        }
        recursivelyTryToMoveTo(agent, graph.start, state.remainingTime)
    }

    private fun cannotWin(agent: Int): Boolean {
        val state = agents[agent]
        var maxScore = currentScore
        if (agent < agents.lastIndex) {
            for (i in graph.valvesByFlow.indices) {
                if (graph.valvesByFlow[i] in opened) continue
                maxScore += graph.valvesByFlow[i].flow * timeLimit
            }
        } else {
            var index = 0
            for (i in graph.valvesByFlow.indices) {
                if (graph.valvesByFlow[i] in opened) continue
                maxScore += graph.valvesByFlow[i].flow * (state.remainingTime - index)
                index++
                if (state.minute + index >= timeLimit) break
            }
        }
        return maxScore < bestScore
    }

    private fun recursivelyTryToMoveTo(agent: Int, valve: Valve, length: Int) {
        val state = agents[agent]
        if (state.minute + length > timeLimit) return

        if (valve != graph.start && !opened.add(valve)) return
        state.minute += length
        val delta = (state.remainingTime) * valve.flow
        currentScore += delta
        val lastPosition = state.currentPosition
        state.currentPosition = valve

        recursivelyTryToMove(agent)

        state.currentPosition = lastPosition
        currentScore -= delta
        state.minute -= length
        if (valve != graph.start) opened.remove(valve)
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
