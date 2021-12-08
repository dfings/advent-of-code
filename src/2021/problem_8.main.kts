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
println(entries.sumOf { it.output.count { it.length in targetLengths } })

// Part 2
val digits = listOf("abcefg", "cf", "acdeg", "acdfg", "bcdf", "abdfg", "abdefg", "acf", "abcdefg", "abcdfg")
fun Entry.decode(): Int {
    val decoder = mutableMapOf<Char, Char>()
    val aOrC = mutableListOf<Char>()
    val dOrG = mutableListOf<Char>()

    val histogram = input.flatMap { it.toList() }.groupingBy { it }.eachCount()
    histogram.forEach { (letter, count) ->
        when (count) {
            4 -> decoder[letter] = 'e'
            6 -> decoder[letter] = 'b'
            7 -> dOrG.add(letter)
            8 -> aOrC.add(letter)
            9 -> decoder[letter] = 'f'
            else -> throw IllegalStateException()
        }
    }

    val one = input.filter { it.length == 2 }.single()
    val c = one.toList().filterNot(decoder::containsKey).single()
    decoder[c] = 'c'
    decoder[aOrC.filterNot { it == c }.single()] = 'a'

    val four = input.filter { it.length == 4 }.single()
    val d = four.toList().filterNot(decoder::containsKey).single()
    decoder[d] = 'd'
    decoder[dOrG.filterNot { it == d }.single()] = 'g'
    
    val decoded = output.map { it.toList().map(decoder::getValue).sorted().joinToString("") }
    return decoded.map(digits::indexOf).joinToString("").toInt()
}

println(entries.sumOf { it.decode() })
