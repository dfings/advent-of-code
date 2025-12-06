#!/usr/bin/env kotlin

val whitespace = "\\s+".toRegex()

fun parse1(input: List<String>): List<List<Long>> {
    val numbers = input.map { it.trim().split(whitespace).map { it.toLong() } }
    return numbers[0].indices.map { i -> numbers.map { it[i] } }
}

fun parse2(input: List<String>): List<List<Long>> {
    val transpose = input[0].indices.map { i -> input.map { it[i] }.joinToString("").trim() }
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
