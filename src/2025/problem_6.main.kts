#!/usr/bin/env kotlin

val whitespace = "\\s+".toRegex()

fun <T> List<List<T>>.transpose() = first().indices.map { i -> map { it[i] } }

fun parse1(input: List<String>): List<List<Long>> =
    input.map { it.trim().split(whitespace).map { it.toLong() } }.transpose()

fun parse2(input: List<String>): List<List<Long>> {
    val transpose = input.map { it.toList() }.transpose().map { it.joinToString("").trim() }
    val numbers = mutableListOf(mutableListOf<Long>())
    for (line in transpose) {
        if (line.isEmpty()) {
            numbers += mutableListOf<Long>()
        } else {
            numbers.last() += line.toLong()
        }
    }
    return numbers
}

fun compute(data: List<List<Long>>, ops: List<String>): Long =
    data.zip(ops).sumOf { (nums, op) -> if (op == "*") nums.reduce(Long::times) else nums.sum() }

fun solve(input: List<String>) {
    val ops = input.last().trim().split(whitespace)
    println(compute(parse1(input.dropLast(1)), ops))
    println(compute(parse2(input.dropLast(1)), ops))
}

solve(java.io.File(args[0]).readLines())
