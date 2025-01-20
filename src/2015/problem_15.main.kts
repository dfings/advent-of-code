#!/usr/bin/env kotlin

import kotlin.math.max

data class Ingredient(
    val properties: List<Long>,
    val calories: Int,
)

val pattern = Regex("""\w+: \w+ (-?\d+), \w+ (-?\d+), \w+ (-?\d+), \w+ (-?\d+), \w+ (-?\d+)""")
fun parse(input: String): Ingredient {
    val (a, b, c, d, e) = pattern.find(input)!!.destructured
    return Ingredient(listOf(a.toLong(), b.toLong(), c.toLong(), d.toLong()), e.toInt())
}

val lines = java.io.File(args[0]).readLines()
val ingredients = lines.map { parse(it) }

fun score(ingredients: List<Ingredient>, counts: List<Int>): Long {
    val properties = LongArray(4)
    for (i in 0..3) {
        for (j in ingredients.indices) {
            properties[i] += ingredients[j].properties[i] * counts[j]
        }
    }
    return properties.map { max(0, it) }.reduce(Long::times)
}

fun calories(ingredients: List<Ingredient>, counts: List<Int>) = 
    ingredients.zip(counts).sumOf { (it, count) -> it.calories * count}

var score1 = 0L
var score2 = 0L
for (a in 0..100) {
    for (b in 0..(100 - a)) {
        for (c in 0..(100 - a - b)) {
            val d = 100 - a - b - c
            val counts = listOf(a, b, c, d)
            val score = score(ingredients, counts)
            score1 = max(score1, score)
            if (calories(ingredients, counts) == 500) {
                score2 = max(score2, score)
            }
        }
    }
}
println(score1)
println(score2)
