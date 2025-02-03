#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

typealias Part = Map<String, Int>
typealias PartRange = Map<String, IntRange>

fun PartRange.combos() = values.map { it.size().toLong() }.reduce(Long::times)
fun PartRange.matches(part: Part) = entries.all { part.getValue(it.key) in it.value }
fun <K, V> Map<K, V>.with(key: K, value: V) = toMutableMap().apply { put(key, value) }

fun IntRange.overlap(other: IntRange) = max(first, other.first)..min(last, other.last)
fun IntRange.size() = if (first > last) 0 else last - first + 1

sealed class Rule(val dest: String) {
    abstract fun apply(partRange: PartRange): Pair<PartRange, PartRange?>
}

class Filter(val key: String, val range: IntRange, val inverse: IntRange, dest: String) : Rule(dest) {
    override fun apply(partRange: PartRange) =
        partRange.with(key, partRange.getValue(key).overlap(range)) to
        partRange.with(key, partRange.getValue(key).overlap(inverse))
}

class Dispatch(dest: String) : Rule(dest)  {
    override fun apply(partRange: PartRange) = partRange to null
}

data class Workflow(val name: String, val rules: List<Rule>)

fun parseRule(input: String): Rule =
    when {
        '>' in input -> {
            val (key, value, dest) = input.split(">", ":")
            Filter(key, (value.toInt() + 1)..4000, 0..value.toInt(), dest)
        }
        '<' in input -> {
            val (key, value, dest) = input.split("<", ":")
            Filter(key, 0..(value.toInt() - 1), value.toInt()..4000, dest)
        }
        else -> Dispatch(input)
    }

fun parseWorkflow(input: String): Workflow {
    val name = input.substringBefore("{")
    val rules = input.substringAfter("{").dropLast(1)
    return Workflow(name, rules.split(",").map { parseRule(it) })
}

fun parsePart(input: String): Part = 
    input.drop(1).dropLast(1).split(",").map { it.split("=").let { it[0] to it[1].toInt() } }.toMap()

fun Map<String, Workflow>.findAccepted(
    workflowName: String, 
    partRange: PartRange,
): List<PartRange> = when {
    workflowName == "R" -> emptyList()
    workflowName == "A" -> listOf(partRange)
    else -> buildList {
        val workflow = getValue(workflowName)
        var current: PartRange? = partRange
        for (rule in workflow.rules) {
            val (pass, fail) = rule.apply(current!!)  
            addAll(findAccepted(rule.dest, pass))
            current = fail
        }
    }
}

fun solve(lines: List<String>) {
    val blank = lines.indexOfFirst { it.isEmpty() }
    val workflows = lines.take(blank).map { parseWorkflow(it) }.associateBy { it.name }
    val parts = lines.drop(blank + 1).map { parsePart(it) }
    val accepted = mutableListOf<Part>()
    val acceptedRanges = workflows.findAccepted("in", "xmas".map { "$it" to 1..4000 }.toMap())
    println(parts.filter { part -> acceptedRanges.any { it.matches(part) } }.sumOf { it.values.sum() })
    println(acceptedRanges.sumOf { it.combos() })
}

solve(java.io.File(args[0]).readLines())
