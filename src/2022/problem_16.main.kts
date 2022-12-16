#!/usr/bin/env kotlin

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

    fun findShortestPath(source: String, target: String): Int? {
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
    var minute = 0
    val turns = ArrayDeque<Pair<Move, Int>>()
    var currentPosition: Valve = graph.start
    var currentScore = 0
    val opened = HashSet<Valve>()

    var bestTurns: ArrayDeque<Pair<Move, Int>>? = null
    var bestScore = 0
    var paths = 0

    fun computeMaxFlow() {
        if (minute == 30 || opened.size == graph.valvesByFlow.size) {
            if (currentScore > bestScore) {
                bestScore = currentScore
                bestTurns = ArrayDeque<Pair<Move, Int>>(turns)
            }
            paths++
            return
        }

        // Detect can't win
        var maxScore = currentScore
        var index = 0
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * (30 - minute - index)
            index++
            if (minute + index >= 30) break
        }
        if (maxScore < bestScore) return

        for (move in graph.moves.getValue(currentPosition) + listOf(Move(graph.start, 30 - minute))) {
            if (minute + move.length > 30) continue
            if (move.valve.name != "AA" && !opened.add(move.valve)) continue
            minute += move.length
            val delta = (30 - minute) * move.valve.flow
            currentScore += delta
            turns.addLast(move to delta)
            currentPosition = move.valve
            computeMaxFlow()
            turns.removeLast()
            currentScore -= delta
            minute -= move.length
            if (move.valve.name != "AA") opened.remove(move.valve)
        }
    }
}

class Part2(graph: Graph) {
    var minute = 0
    var eminute = 0
    val turns = ArrayDeque<Pair<Move, Int>>()
    val eturns = ArrayDeque<Pair<Move, Int>>()
    var currentPosition: Valve = graph.start
    var currentEPosition: Valve = graph.start
    var currentScore = 0
    val opened = HashSet<Valve>()

    var bestTurns: ArrayDeque<Pair<Move, Int>>? = null
    var bestETurns: ArrayDeque<Pair<Move, Int>>? = null
    var bestScore = 0
    var paths = 0

    fun computeMaxFlow() {
        if (minute == 26) {
            computeMaxEFlow()
            return
        }
        if (opened.size == graph.valvesByFlow.size) {
            if (currentScore > bestScore) {
                bestScore = currentScore
                bestTurns = ArrayDeque<Pair<Move, Int>>(turns)
            }
            paths++
            return
        }

        // Detect can't win
        var maxScore = currentScore
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * 26
        }
        if (maxScore < bestScore) return

        val lastPos = currentPosition
        for (move in graph.moves.getValue(currentPosition) + listOf(Move(graph.start, 26 - minute))) {
            if (minute + move.length > 26) continue
            if (move.valve.name != "AA" && !opened.add(move.valve)) continue
            minute += move.length
            val delta = (26 - minute) * move.valve.flow
            currentScore += delta
            turns.addLast(move to delta)
            currentPosition = move.valve
            computeMaxFlow()
            currentPosition = lastPos
            turns.removeLast()
            currentScore -= delta
            minute -= move.length
            if (move.valve.name != "AA") opened.remove(move.valve)
        }
    }

    fun computeMaxEFlow() {
        if (eminute == 26 || opened.size == graph.valvesByFlow.size) {
            if (currentScore > bestScore) {
                bestScore = currentScore
                bestTurns = ArrayDeque<Pair<Move, Int>>(turns)
                bestETurns = ArrayDeque<Pair<Move, Int>>(eturns)
            }
            paths++
            return
        }

        // Detect can't win
        var maxScore = currentScore
        var index = 0
        for (i in graph.valvesByFlow.indices) {
            if (graph.valvesByFlow[i] in opened) continue
            maxScore += graph.valvesByFlow[i].flow * (26 - eminute - index)
            index++
            if (eminute + index >= 26) break
        }
        if (maxScore < bestScore) return

        val lastPos = currentEPosition
        for (move in graph.moves.getValue(currentEPosition) + listOf(Move(graph.start, 26 - eminute))) {
            if (eminute + move.length > 26) continue
            if (move.valve.name != "AA" && !opened.add(move.valve)) continue
            eminute += move.length
            val delta = (26 - eminute) * move.valve.flow
            currentScore += delta
            eturns.addLast(move to delta)
            currentEPosition = move.valve
            computeMaxEFlow()
            currentEPosition = lastPos
            eturns.removeLast()
            currentScore -= delta
            eminute -= move.length
            if (move.valve.name != "AA") opened.remove(move.valve)
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
part2.bestETurns?.forEachIndexed { i, it -> println("${i + 1}: $it") }
println(part2.bestScore)
