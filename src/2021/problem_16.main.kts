#!/usr/bin/env kotlin

// AST definition
sealed class Packet(val version: Int, val length: Int)

class Literal(version: Int, length: Int, val value: Long) : Packet(version, length)

class Operator(version: Int, val typeId: Int, length: Int, val subpackets: List<Packet>) : Packet(version, length)

fun Iterator<Char>.take(n: Int) = (1..n).map { next() }

fun List<Char>.binaryToInt() = joinToString("").toInt(radix = 2)
fun List<Char>.binaryToLong() = joinToString("").toLong(radix = 2)

fun parseLiteral(version: Int, iter: Iterator<Char>): Literal {
    var blockCount = 0
    val encoded = mutableListOf<Char>()
    do {
        blockCount++
        val partial = iter.take(5)
        encoded += partial.drop(1)
    } while (partial.first() == '1')
    return Literal(version, 6 + blockCount * 5, encoded.binaryToLong())
}

fun parseLengthDelimitedOperator(version: Int, typeId: Int, iter: Iterator<Char>): Operator {
    val length = iter.take(15).binaryToInt()
    val subpackets = mutableListOf<Packet>() 
    var remaining = length
    while (remaining > 0) {
        subpackets.add(parsePacket(iter))
        remaining -= subpackets.last().length
    }
    return Operator(version, typeId, 22 + length, subpackets)
}

fun parsePacketDelimitedOperator(version: Int, typeId: Int, iter: Iterator<Char>): Operator {
    val subpacketCount = iter.take(11).binaryToInt()
    val subpackets = (1..subpacketCount).map { parsePacket(iter) }
    return Operator(version, typeId, 18 + subpackets.sumOf { it.length }, subpackets)
}

fun parsePacket(iter: Iterator<Char>): Packet {
    val version = iter.take(3).binaryToInt()
    val typeId = iter.take(3).binaryToInt()
    return when {
        typeId == 4 -> parseLiteral(version, iter)
        iter.next() == '0' -> parseLengthDelimitedOperator(version, typeId, iter)
        else -> parsePacketDelimitedOperator(version, typeId, iter)
    }
}

fun Char.hexToBinaryString() = Integer.toBinaryString((1 shl 4) or digitToInt(radix = 16)).drop(1)

val input = java.io.File(args[0]).readLines().single()
val binary = input.map { it.hexToBinaryString() }.joinToString("").toList()
val packet = parsePacket(binary.iterator())

fun sumVersions(packet: Packet): Int = when (packet) {
    is Literal -> packet.version
    is Operator -> packet.version + packet.subpackets.map { sumVersions(it) }.sum()
    else -> error("")
}
println(sumVersions(packet))

fun applyOperators(packet: Packet): Long = when (packet) {
    is Literal -> packet.value
    is Operator -> {
        val results = packet.subpackets.map { applyOperators(it) }
        when (packet.typeId) {
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
println(applyOperators(packet))
