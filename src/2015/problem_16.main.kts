#!/usr/bin/env kotlin

val target = mapOf(
    "children" to 3,
    "cats" to 7,
    "samoyeds" to 2,
    "pomeranians" to 3,
    "akitas" to 0,
    "vizslas" to 0,
    "goldfish" to 5,
    "trees" to 3,
    "cars" to 2,
    "perfumes" to 1,
)

val pattern = Regex("""Sue \d+: (\w+): (\d+), (\w+): (\d+), (\w+): (\d+)""")
fun parse(input: String): Map<String, Int> {
    val (a1, a2, b1, b2, c1, c2) = pattern.find(input)!!.destructured
    return mapOf(a1 to a2.toInt(), b1 to b2.toInt(), c1 to c2.toInt())
}

fun Map<String, Int>.matches() = entries.all { (k, v) -> target[k] == v }
fun Map<String, Int>.matches2() = entries.all { (k, v) -> when(k) {
        "cats", "trees" -> target[k]!! < v
        "pomeranians", "goldfish" -> target[k]!! > v
        else -> target[k] == v 
    }
}

val lines = java.io.File(args[0]).readLines()
val sues = lines.map { parse(it) }
println(sues.indexOfFirst { it.matches() } + 1)
println(sues.indexOfFirst { it.matches2() } + 1)
