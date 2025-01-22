#!/usr/bin/env kotlin

data class Blueprint(
    val oreForOre: Int,
    val oreForClay: Int,
    val oreForObsidian: Int,
    val clayForObsidian: Int,
    val oreForGeode: Int,
    val obsidianForGeode: Int,
) {
    val maxOre = listOf(oreForOre, oreForClay, oreForObsidian, oreForGeode).max()
}

fun parse(input: String): Blueprint {
    val pattern = Regex("""\d+:.*(\d+).* (\d+).* (\d+).* (\d+).* (\d+).* (\d+)""")
    val (a, b, c, d, e, f) = pattern.find(input)!!.destructured
    return Blueprint(a.toInt(), b.toInt(), c.toInt(), d.toInt(), e.toInt(), f.toInt())
}

data class State(
    val b: Blueprint,
    val minute: Int = 0,
    val oreRobots: Int = 1,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobots: Int = 0,
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geodes: Int = 0,
)

fun State.canBuildOre() = oreRobots < b.maxOre && ore >= b.oreForOre
fun State.canBuildClay() = clayRobots < b.clayForObsidian && ore >= b.oreForClay
fun State.canBuildObsidian() = 
    obsidianRobots < b.obsidianForGeode && 
    ore >= b.oreForObsidian && 
    clay >= b.clayForObsidian
fun State.canBuildGeode() = ore >= b.oreForGeode && obsidian >= b.obsidianForGeode

fun State.buildOre() = copy(oreRobots = oreRobots + 1, ore = ore - b.oreForOre)
fun State.buildClay() = copy(clayRobots = clayRobots + 1, ore = ore - b.oreForClay)
fun State.buildObsidian() = copy(
    obsidianRobots = obsidianRobots + 1, 
    ore = ore - b.oreForObsidian, 
    clay = clay - b.clayForObsidian,
)
fun State.buildGeode() = copy(
    geodeRobots = geodeRobots + 1, 
    ore = ore - b.oreForGeode, 
    obsidian = obsidian - b.obsidianForGeode,
)

fun State.produce() = copy(
    minute = minute + 1,
    ore = ore + oreRobots,
    clay = clay + clayRobots,
    obsidian = obsidian + obsidianRobots,
    geodes = geodes + geodeRobots,
)

fun State.advanceUntil(limit: Int, isReady: State.() -> Boolean): State? {
    var current = this
    while (!current.isReady() && current.minute < limit) {
        current = current.produce()
    }
    return if (current.isReady() && current.minute < limit) current.produce() else null
}

fun State.advance(limit: Int) = buildList<State> {
    if (obsidianRobots > 0) advanceUntil(limit) { canBuildGeode() }?.let { add(it.buildGeode()) }
    if (clayRobots > 0) advanceUntil(limit) { canBuildObsidian() }?.let { add(it.buildObsidian()) }
    advanceUntil(3 * limit / 4) { canBuildClay() }?.let { add(it.buildClay()) }
    advanceUntil(limit / 2) { canBuildOre() }?.let { add(it.buildOre()) }
}

fun findBestEndState(current: State, limit: Int): State? {
    if (current.minute == limit) {
        return current
    }
    return current.advance(limit)
        .mapNotNull { findBestEndState(it, limit) }
        .maxByOrNull { it.geodes }
}

val lines = java.io.File(args[0]).readLines()
val blueprints = lines.map { parse(it) }

val bestStates = blueprints.map { findBestEndState(State(it), 24)!! }
println(bestStates.mapIndexed { i, it -> (i + 1) * it.geodes }.sum())

val bestStates2 = blueprints.take(3).map { findBestEndState(State(it), 32)!! }
println(bestStates2.map { it.geodes }.reduce(Int::times))
