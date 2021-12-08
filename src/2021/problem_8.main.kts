#!/usr/bin/env kotlin

data class Entry(val input: List<String>, val output: List<String>)

fun String.sorted(): String = toList().sorted().joinToString("")
fun String.toEntry(): Entry {
    val chunks = split(" | ").map { it.split(" ").map { it.sorted() } }
    return Entry(chunks[0], chunks[1])
}
val entries = java.io.File(args[0]).readLines().map { it.toEntry() }

// Part 1
val targetLengths = setOf(2, 3, 4, 7)
println(entries.map { it.output }.sumOf { it.count {  it.length in targetLengths } })

// Part 2
val digits = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")
fun Entry.decode(): Int {
    val decoder = mutableMapOf<Char, Char>()
    val aOrC = mutableSetOf<Char>()
    val dOrG = mutableSetOf<Char>()

    val histogram = input.flatMap { it.toList() }.groupingBy { it }.eachCount()
    histogram.forEach { (key, value) ->
        when (value) {
            4 -> decoder[key] = 'e'
            6 -> decoder[key] = 'b'
            7 -> dOrG.add(key)
            8 -> aOrC.add(key)
            9 -> decoder[key] = 'f'
            else -> throw IllegalStateException()
        }
    }

    val one = checkNotNull(input.find { it.length == 2 })
    val c = one.toList().filterNot(decoder::containsKey).single()
    decoder[c] = 'c'
    decoder[(aOrC - setOf(c)).single()] = 'a'

    val four = checkNotNull(input.find { it.length == 4 })
    val d = four.toList().filterNot(decoder::containsKey).single()
    decoder[d] = 'd'
    decoder[(dOrG - setOf(d)).single()] = 'g'
    
    val decoded = output.map { it.toList().map(decoder::getValue).sorted().joinToString("") }
    return decoded.map(digits::indexOf).joinToString("").toInt()
}

println(entries.sumOf { it.decode() })
