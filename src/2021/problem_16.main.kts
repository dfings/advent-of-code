#!/usr/bin/env kotlin

import kotlin.text.removeSurrounding

// Utility
fun Iterator<Char>.take(n: Int) = (1..n).map { next() }
fun List<Char>.binaryToInt() = joinToString("").toInt(radix = 2)
fun List<Char>.binaryToLong() = joinToString("").toLong(radix = 2)
fun Char.hexToBinaryString() = Integer.toBinaryString((1 shl 4) or digitToInt(radix = 16)).drop(1)

// AST definition
sealed class Packet(val version: Int, val length: Int)
class Literal(version: Int, length: Int, val value: Long) : Packet(version, length)
class Operator(version: Int, val typeId: Int, length: Int, val subpackets: List<Packet>) : Packet(version, length)

fun List<Packet>.totalLength() = sumOf { it.length }

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
    return Literal(version, 6 + (encoded.size / 4) * 5, encoded.binaryToLong())
}

fun Iterator<Char>.parseLengthDelimitedOperator(version: Int, typeId: Int): Operator {
    val subpackets = mutableListOf<Packet>() 
    val length = take(15).binaryToInt()
    while (subpackets.totalLength() < length) {
        subpackets.add(parsePacket())
    }
    return Operator(version, typeId, 22 + subpackets.totalLength(), subpackets)
}

fun Iterator<Char>.parsePacketDelimitedOperator(version: Int, typeId: Int): Operator {
    val subpacketCount = take(11).binaryToInt()
    val subpackets = (1..subpacketCount).map { parsePacket() }
    return Operator(version, typeId, 18 + subpackets.totalLength(), subpackets)
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
    is Literal -> "${value}"
    is Operator -> {
        val results = subpackets.map { it.render() }
        when (typeId) {
            0 -> if (results.size == 1) results[0] else "(${results.joinToString(" + ")})"
            1 -> if (results.size == 1) results[0] else "(${results.joinToString(" * ")})"
            2 -> "min(${results.joinToString(", ").removeSurrounding("(", ")")})"
            3 -> "max(${results.joinToString(", ").removeSurrounding("(", ")")})"
            5 -> "(${results[0]} > ${results[1]})"
            6 -> "(${results[0]} < ${results[1]})"
            7 -> "(${results[0]} == ${results[1]})"
            else -> error("")
        }
    }
    else -> error("")
}
println(packet.render())
