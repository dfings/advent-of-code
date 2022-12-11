#!/usr/bin/env kotlin

class Monkey(
    val items: MutableList<Long>,
    val worryIncreaser: (Long) -> Long,
    val test: Long,
    val passTrue: Int,
    val passFalse: Int,
    var count: Long = 0L
)

class Simulation(val monkeys: List<Monkey>, val worryReducer: (Long) -> Long) {
    fun runRound() {
        monkeys.forEach { it.inspectAll() }
    }

    fun Monkey.inspectAll() {
        repeat(items.size) {
            count++
            val worry = worryReducer(worryIncreaser(items.removeFirst()))
            val passTo = if (worry % test == 0L) passTrue else passFalse
            monkeys[passTo].items.add(worry)
        }
    }
}

fun parseWorryIncreaser(spec: String): (Long) -> Long {
    if (spec == "old * old") {
        return { it * it }
    }
    val value = spec.drop(6).toInt()
    if (spec.startsWith("old +")) {
        return { it + value }
    }
    return { it * value }
}

fun runSimulation(iterations: Int, divideWorry: Boolean) {
    val lines = java.io.File(args[0]).readLines()
    val monkeys = lines.chunked(7).map { spec ->
        Monkey(
            items = ArrayDeque(spec[1].drop(18).split(", ").map { it.toLong() }),
            worryIncreaser = parseWorryIncreaser(spec[2].drop(19)),
            test = spec[3].drop(21).toLong(),
            passTrue = spec[4].drop(29).toInt(),
            passFalse = spec[5].drop(30).toInt()
        )
    }

    val scale = monkeys.map { it.test }.reduce(Long::times)
    val simulation = Simulation(monkeys) { if (divideWorry) it / 3 else it % scale }
    repeat(iterations) {
        simulation.runRound()
    }
    println(monkeys.map { it.count }.sorted().takeLast(2).reduce(Long::times))
}

runSimulation(iterations = 20, divideWorry = true)
runSimulation(iterations = 10000, divideWorry = false)
