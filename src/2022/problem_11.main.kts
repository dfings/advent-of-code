#!/usr/bin/env kotlin

class Item(var worry: Long)
class Monkey(val items: MutableSet<Item>, val worryIncreaser: (Long) -> Long, val test: Long, val reduceWorry: Boolean) {
    lateinit var trueMonkey: Monkey
    lateinit var falseMonkey: Monkey
    var count = 0L

    fun inspectAll() = items.toList().forEach(this::inspect)
    fun inspect(item: Item) {
        count++
        item.worry = worryIncreaser(item.worry)
        if (reduceWorry) {
            item.worry /= 3
        }
        val passTo = if (item.worry % test == 0L) trueMonkey else falseMonkey
        passTo.items.add(item)
        items.remove(item)
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

    val monkeys = mutableListOf<Monkey>()
    val allItems = mutableListOf<Item>()
    lines.chunked(7).forEach { spec ->
        val items = spec[1].drop(18).split(", ").map { Item(it.toLong()) }
        val worryIncreaser = parseWorryIncreaser(spec[2].drop(19))
        val test = spec[3].drop(21).toLong()
        monkeys.add(Monkey(items.toMutableSet(), worryIncreaser, test, divideWorry))
        allItems.addAll(items)
    }
    lines.chunked(7).forEachIndexed { i, spec ->
        monkeys[i].trueMonkey = monkeys[spec[4].drop(29).toInt()]
        monkeys[i].falseMonkey = monkeys[spec[5].drop(30).toInt()]
    }

    val scale = monkeys.map { it.test }.reduce(Long::times)
    repeat(iterations) {
        monkeys.forEach { it.inspectAll() }
        allItems.forEach { it.worry %= scale }
    }
    println(monkeys.map { it.count }.sorted().takeLast(2).reduce(Long::times))
}

runSimulation(iterations = 20, divideWorry = true)
runSimulation(iterations = 10000, divideWorry = false)
