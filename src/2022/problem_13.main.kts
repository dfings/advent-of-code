#!/usr/bin/env kotlin

sealed interface PacketData : Comparable<PacketData>

data class ListValue(val values: List<PacketData>) : PacketData {
    override fun toString() = values.toString()
    override operator fun compareTo(other: PacketData): Int = when (other) {
            is ListValue -> values.zip(other.values).asSequence()
                .map { (a, b) -> a.compareTo(b) }
                .firstOrNull { it != 0 } 
                ?: values.size.compareTo(other.values.size)
            else -> compareTo(ListValue(listOf(other)))
    }
}

data class IntValue(val value: Int) : PacketData {
    override fun toString() = value.toString()
    override operator fun compareTo(other: PacketData): Int = when (other) {
        is IntValue -> value.compareTo(other.value)
        else -> ListValue(listOf(this)).compareTo(other)
    }
}

fun String.parsePacketData(): PacketData {
    if (!startsWith("[")) return IntValue(toInt())
    val innerData = mutableListOf<PacketData>()
    val remaining = ArrayDeque<Char>(drop(1).toList())
    while (remaining.size > 1) {
        val innerLine = mutableListOf<Char>()
        var braceCounter = 0
        while (true) {
            val character = remaining.removeFirst()!!
            when (character) {
                '[' -> braceCounter++
                ']' -> {
                    braceCounter--
                    if (braceCounter < 0) break
                }
                ',' -> if (braceCounter == 0) break
            }
            innerLine.add(character)
        }
        innerData.add(innerLine.joinToString("").parsePacketData())
    }
    return ListValue(innerData)
}

val lines = java.io.File(args[0]).readLines()
val packets = lines.chunked(3).flatMap { it.take(2).map { it.parsePacketData() } }
println(packets.chunked(2).mapIndexed { i, it -> if (it[0] < it[1]) i + 1 else 0 }.sum())

val two = "[[2]]".parsePacketData()
val six = "[[6]]".parsePacketData()
val sortedPackets = (packets + listOf(two, six)).sorted()
println((sortedPackets.indexOf(two) + 1) * (sortedPackets.indexOf(six) + 1))
