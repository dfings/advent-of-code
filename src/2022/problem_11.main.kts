#!/usr/bin/env kotlin

class Monkey(
    val items: MutableList<Long>,
    val increaseWorry: (Long) -> Long,
    val test: Long,
    val passTrue: Int,
    val passFalse: Int,
    var count: Long = 0L
)

class Simulation(val monkeys: List<Monkey>, val reduceWorry: (Long) -> Long) {
    fun runRound() = monkeys.forEach { it.inspectAll() }
    fun Monkey.inspectAll() = repeat(items.size) { inspectOne() }
    fun Monkey.inspectOne() {
        count++
        val worry = reduceWorry(increaseWorry(items.removeFirst()))
        val passTo = if (worry % test == 0L) passTrue else passFalse
        monkeys[passTo].items.add(worry)
    }
}

fun parseIncreaseWorry(tokens: List<String>): (Long) -> Long {
    return when {
        tokens[1] == "old" -> { x: Long -> x * x }
        tokens[0] == "+" -> tokens[1].toInt().let { { x: Long -> x + it }}
        else -> tokens[1].toInt().let { { x: Long -> x * it }}
    }
}

fun runSimulation(lines: List<String>, iterations: Int, divideWorry: Boolean) {
    val monkeys = lines.chunked(7).map { line ->
        Monkey(
            items = ArrayDeque(line[1].drop(18).split(", ").map { it.toLong() }),
            increaseWorry = parseIncreaseWorry(line[2].drop(23).split(" ")),
            test = line[3].drop(21).toLong(),
            passTrue = line[4].drop(29).toInt(),
            passFalse = line[5].drop(30).toInt()
        )
    }

    val scale = monkeys.map { it.test }.reduce(Long::times)
    val simulation = Simulation(monkeys) { if (divideWorry) it / 3 else it % scale }
    repeat(iterations) {
        simulation.runRound()
    }
    println(monkeys.map { it.count }.sorted().takeLast(2).reduce(Long::times))
}

val lines = java.io.File(args[0]).readLines()
runSimulation(lines, iterations = 20, divideWorry = true)
runSimulation(lines, iterations = 10000, divideWorry = false)
