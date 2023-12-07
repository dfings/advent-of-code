#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Hand(val cards: List<Int>, val type: Int, val bid: Int)

val handComparator =
    compareBy<Hand> { it.type }
        .thenBy { it.cards[0] }.thenBy { it.cards[1] }.thenBy { it.cards[2] }
        .thenBy { it.cards[3] }.thenBy { it.cards[4] }

val cardMap = mapOf('T' to 10, 'J' to 11, 'Q' to 12, 'K' to 13, 'A' to 14)

fun String.toCards(): List<Int> = map { cardMap[it] ?: it - '0' }

fun List<Int>.toType(): Int {
    val noJokers = filter { it != -1 }
    return noJokers.groupingBy { it }.eachCount().let {
        when {
            it.size <= 1 -> 7 // 5 of a kind
            it.values.any { it == noJokers.size - 1 } -> 6 // 4 of a kind
            it.size == 2 -> 5 // Full house
            it.values.any { it == noJokers.size - 2 } -> 4 // 3 of a kind
            it.size == 3 -> 3 // 2 pair
            it.size == 4 -> 2 // Pair
            else -> 1 // High card
        }
    }
}

fun Hand.withJokers(): Hand {
    val newCards = cards.map { if (it == 11) -1 else it }
    return copy(cards = newCards, type = newCards.toType())
}

fun List<Hand>.score() = foldIndexed(0L) { index, acc, it -> acc + (index + 1) * it.bid }

val hands = lines.map { it.split(" ").let { Hand(it[0].toCards(), it[0].toCards().toType(), it[1].toInt()) } }
println(hands.sortedWith(handComparator).score())
println(hands.map { it.withJokers() }.sortedWith(handComparator).score())
