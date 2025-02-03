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

data class Rule(val key: String, val range: IntRange, val inverse: IntRange, val dest: String)
data class Workflow(val name: String, val rules: List<Rule>)

fun parseRule(input: String): Rule =
    when {
        '>' in input -> {
            val (key, value, dest) = input.split(">", ":")
            Rule(key, (value.toInt() + 1)..4000, 0..value.toInt(), dest)
        }
        '<' in input -> {
            val (key, value, dest) = input.split("<", ":")
            Rule(key, 0..(value.toInt() - 1), value.toInt()..4000, dest)
        }
        else -> Rule("", 0..4000, -1..-1, input)
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
        var current = partRange
        for (rule in workflow.rules) {
            if (rule.key.isEmpty()) {
                addAll(findAccepted(rule.dest, current))
            } else {
                val range = current.getValue(rule.key)
                val next = current.toMutableMap()
                next.put(rule.key, range.overlap(rule.range))
                addAll(findAccepted(rule.dest, next.toMap()))
                next.put(rule.key, range.overlap(rule.inverse))                
                current = next
            }
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
