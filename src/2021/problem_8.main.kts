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

    val one = input.single { it.length == 2 }
    val c = one.toList().single { it !in decoder.keys }
    decoder[c] = 'c'
    decoder[aOrC.single { it != c }] = 'a'

    val four = input.single { it.length == 4 }
    val d = four.toList().single { it !in decoder.keys }
    decoder[d] = 'd'
    decoder[dOrG.single { it != d }] = 'g'
    
    val decoded = output.map { it.toList().map(decoder::getValue).sorted().joinToString("") }
    return decoded.map(digits::indexOf).joinToString("").toInt()
}

println(entries.sumOf { it.decode() })
