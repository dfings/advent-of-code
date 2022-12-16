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

class Part1(graph: Graph) {
    val timeLimit = 30
    val me = AgentState(graph.start, timeLimit)

    val opened = HashSet<Valve>()
    var currentScore = 0
    var bestScore = 0

    fun computeMaxFlow() {
        if (me.minute == timeLimit || opened.size == graph.valveCount) {
            bestScore = max(bestScore, currentScore)
            return
        }

        if (cannotWin()) return

        for (move in graph.moves.getValue(me.currentPosition)) {
            recursivelyTryToMoveTo(move.valve, move.length)
        }
        recursivelyTryToMoveTo(graph.start, me.remainingTime)
    }

    private fun cannotWin(): Boolean {
        var maxScore = currentScore
        var index = 0
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * (me.remainingTime - index)
            index++
            if (me.minute + index >= timeLimit) break
        }
        return maxScore < bestScore
    }

    private fun recursivelyTryToMoveTo(valve: Valve, length: Int) {
        if (me.minute + length > timeLimit) return
        if (valve != graph.start && !opened.add(valve)) return
        
        me.minute += length
        val delta = (me.remainingTime) * valve.flow
        currentScore += delta
        val lastPosition = me.currentPosition
        me.currentPosition = valve
        
        computeMaxFlow()
        
        me.currentPosition = lastPosition
        currentScore -= delta
        me.minute -= length
        if (valve != graph.start) opened.remove(valve)
    }
}

class Part2(graph: Graph) {
    val timeLimit = 26
    val me = AgentState(graph.start, timeLimit)
    val elephant = AgentState(graph.start, timeLimit)

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

        if (me.minute == timeLimit) {
            recursivelyTryToMoveElephant()
            return
        }

        if (cannotWin()) return

        for (move in graph.moves.getValue(me.currentPosition)) {
            recursivelyTryToMoveTo(move.valve, move.length)
        }
        recursivelyTryToMoveTo(graph.start, me.remainingTime)
    }

    private fun cannotWin(): Boolean {
        var maxScore = currentScore
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * timeLimit
        }
        return maxScore < bestScore
    }

    private fun recursivelyTryToMoveTo(valve: Valve, length: Int) {
        if (me.minute + length > timeLimit) return
        if (!tryOpen(valve)) return
        
        me.minute += length
        val delta = me.remainingTime * valve.flow
        currentScore += delta
        val lastPosition = me.currentPosition
        me.currentPosition = valve
        
        recursivelyTryToMove()
        
        me.currentPosition = lastPosition
        currentScore -= delta
        me.minute -= length
        tryClose(valve)
    }

    private fun recursivelyTryToMoveElephant() {
        if (elephant.minute == timeLimit || opened.size == graph.valveCount) {
            bestScore = max(bestScore, currentScore)
            return
        }

        if (elephantCannotWin()) return

        for (move in graph.moves.getValue(elephant.currentPosition)) {
            recursivelyTryToMoveElephantTo(move.valve, move.length)
        }
        recursivelyTryToMoveElephantTo(graph.start, elephant.remainingTime)
    }

    private fun elephantCannotWin(): Boolean {
        var maxScore = currentScore
        var index = 0
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * (elephant.remainingTime - index)
            index++
            if (elephant.minute + index >= timeLimit) break
        }
        return maxScore < bestScore
    }

    private fun recursivelyTryToMoveElephantTo(valve: Valve, length: Int) {
        if (elephant.minute + length > timeLimit) return
        if (!tryOpen(valve)) return
        
        elephant.minute += length
        val delta = elephant.remainingTime * valve.flow
        currentScore += delta
        val lastElephantPosition = elephant.currentPosition
        elephant.currentPosition = valve
        
        recursivelyTryToMoveElephant()

        elephant.currentPosition = lastElephantPosition
        currentScore -= delta
        elephant.minute -= length
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
