#!/usr/bin/env kotlin

// Utility
fun <T> Iterator<T>.take(n: Int) = (1..n).map { next() }
fun List<Char>.binaryToInt() = joinToString("").toInt(radix = 2)
fun List<Char>.binaryToLong() = joinToString("").toLong(radix = 2)
fun Char.hexToBinaryString() = Integer.toBinaryString(digitToInt(radix = 16)).padStart(4, '0')

// AST definition
sealed class Packet(val version: Int)
class Literal(version: Int, val value: Long) : Packet(version)
class Operator(version: Int, val typeId: Int, val subpackets: List<Packet>) : Packet(version)

fun Iterator<Char>.parsePacket(): Packet {
    val version = take(3).binaryToInt()
    val typeId = take(3).binaryToInt()
    return when {
        typeId == 4 -> parseLiteral(version)
        next() == '0' -> parseLengthDelimitedOperator(version, typeId)
        else -> parsePacketDelimitedOperator(version, typeId)
    }
}

fun Iterator<Char>.parseLiteral(version: Int): Literal {
    val encoded = mutableListOf<Char>()
    do {
        val partial = take(5)
        encoded += partial.drop(1)
    } while (partial.first() == '1')
    return Literal(version, encoded.binaryToLong())
}

fun Iterator<Char>.parseLengthDelimitedOperator(version: Int, typeId: Int): Operator {
    val length = take(15).binaryToInt()
    val subiter = take(length).iterator()
    val subpackets = mutableListOf<Packet>() 
    while (subiter.hasNext()) {
        subpackets.add(subiter.parsePacket())
    }
    return Operator(version, typeId, subpackets)
}

fun Iterator<Char>.parsePacketDelimitedOperator(version: Int, typeId: Int): Operator {
    val subpacketCount = take(11).binaryToInt()
    val subpackets = (1..subpacketCount).map { parsePacket() }
    return Operator(version, typeId, subpackets)
}

val input = java.io.File(args[0]).readLines().single()
val binary = input.map { it.hexToBinaryString() }.joinToString("").toList()
val packet = binary.iterator().parsePacket()

fun Packet.versionSum(): Int = when (this) {
    is Literal -> version
    is Operator -> version + subpackets.map { it.versionSum() }.sum()
    else -> error("")
}
println(packet.versionSum())

fun Packet.evaluate(): Long = when (this) {
    is Literal -> value
    is Operator -> {
        val results = subpackets.map { it.evaluate() }
        when (typeId) {
            0 -> results.sum()
            1 -> results.reduce(Long::times)
            2 -> results.minOf { it }
            3 -> results.maxOf { it }
            5 -> if (results[0] > results[1]) 1 else 0
            6 -> if (results[0] < results[1]) 1 else 0
            7 -> if (results[0] == results[1]) 1 else 0
            else -> error("")
        }
    }
    else -> error("")
}
println(packet.evaluate())

fun Packet.render(): String = when (this) {
    is Literal -> "$value"
    is Operator -> {
        val results = subpackets.map { it.render() }.joinToString(" ")
        val op = when (typeId) {
            0 -> "+"
            1 -> "*"
            2 -> "min"
            3 -> "max"
            5 -> ">"
            6 -> "<"
            7 -> "=="
            else -> error("")
        }
        "($op $results)"
    }
    else -> error("")
}
println(packet.render())
