#!/usr/bin/env kotlin

typealias Bag = List<Long>

val bagComparator = Comparator.comparing<Bag, _> { it.size }
    .thenComparing { it.reduce(Long::times) }

fun findAllValidBags(weights: List<Long>, bag: Bag, targetWeight: Long): List<Bag> {
    val totalWeight = bag.sum()
    return when {
        totalWeight == targetWeight -> listOf(bag)
        totalWeight > targetWeight -> emptyList()
        else -> weights.flatMapIndexed { i, it -> 
            findAllValidBags(weights.subList(i + 1, weights.size), bag + it, targetWeight)
        }
    }
}

val lines = java.io.File(args[0]).readLines()
val weights = lines.map { it.toLong() }

val validBags1 = findAllValidBags(weights, emptyList(), weights.sum() / 3)
println(validBags1.minWith(bagComparator).reduce(Long::times))

val validBags2 = findAllValidBags(weights, emptyList(), weights.sum() / 4)
println(validBags2.minWith(bagComparator).reduce(Long::times))
