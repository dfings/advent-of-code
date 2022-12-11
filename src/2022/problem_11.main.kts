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

fun parseIncreaseWorry(spec: String): (Long) -> Long {
    if (spec == "old * old") {
        return { it * it }
    }
    val value = spec.drop(6).toInt()
    if (spec.startsWith("old +")) {
        return { it + value }
    }
    return { it * value }
}

fun runSimulation(lines: List<String>, iterations: Int, divideWorry: Boolean) {
    val monkeys = lines.chunked(7).map { line ->
        Monkey(
            items = ArrayDeque(line[1].drop(18).split(", ").map { it.toLong() }),
            increaseWorry = parseIncreaseWorry(line[2].drop(19)),
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
