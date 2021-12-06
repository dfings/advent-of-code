#!/usr/bin/env kotlin

fun runGenerations(start: Map<Int, Long>, generations: Int): Map<Int, Long> =
    generateSequence(0 to start) { (i, fish) ->
        if (i == generations) {
            null
        } else {
            i + 1 to fish.flatMap { (timer, count) ->
                if (timer == 0) listOf(8 to count, 6 to count) else  listOf(timer - 1 to count)
            }.groupBy { it.first }.mapValues { it.value.sumOf { it.second } }
        }
    }.last().second
    
val input = java.io.File(args[0]).readLines().first().split(",")
val start = input.groupingBy { it.toInt() }.eachCount().mapValues { it.value.toLong() }
println(runGenerations(start, 80).values.sum())
println(runGenerations(start, 256).values.sum())
