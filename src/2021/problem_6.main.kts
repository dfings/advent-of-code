#!/usr/bin/env kotlin

fun runGenerations(start: Map<Int, Long>, generations: Int): Map<Int, Long> =
    generateSequence(0 to start) { (i, fish) ->
        if (i == generations) {
            null
        } else {
            val next = mutableMapOf<Int, Long>()
            fish.forEach { timer, count ->
                if (timer == 0) {
                    next.put(8, count)
                    next.put(6, count + next.getOrDefault(6, 0L))
                } else {
                    next.put(timer - 1, count + next.getOrDefault(timer - 1, 0L))
                }
            }
            i + 1 to next
        }
    }.last().second
    
val input = java.io.File(args[0]).readLines().first().split(",")
val start = input.groupingBy { it.toInt() }.eachCount().mapValues { it.value.toLong() }
println(runGenerations(start, 80).values.sum())
println(runGenerations(start, 256).values.sum())
