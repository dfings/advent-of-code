#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

data class Hand(val cards: List<Int>, val type: Int, val bid: Int)

val handComparator =
    Comparator<Hand> { a, b -> a.type - b.type }
        .thenBy { it.cards[0] }.thenBy { it.cards[1] }.thenBy { it.cards[2] }
        .thenBy { it.cards[3] }.thenBy { it.cards[4] }

val cardMap = mapOf('T' to 10, 'J' to 11, 'Q' to 12, 'K' to 13, 'A' to 14)

fun String.toCards(): List<Int> = map { cardMap[it] ?: it - '0' }

fun List<Int>.toType(): Int {
    val withoutJokers = filter { it != -1 }
    return withoutJokers.groupingBy { it }.eachCount().let {
        when {
            it.size <= 1 -> 7
            it.values.any { it == withoutJokers.size - 1 } -> 6
            it.size == 2 -> 5
            it.values.any { it == 3 - (5 - withoutJokers.size) } -> 4
            it.size == 3 -> 3
            it.size == 4 -> 2
            else -> 1
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
