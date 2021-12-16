#!/usr/bin/env kotlin

// AST definition
sealed class Packet(val version: Int, val typeId: Int, val length: Int)

class Literal(version: Int, typeId: Int, length: Int, val value: Long) : Packet(version, typeId, length)

class Operator(version: Int, typeId: Int, length: Int, val subpackets: List<Packet>)  : Packet(version, typeId, length)

class PacketScanner(val iter: Iterator<Char>) {
    var count = 0
    fun take(n: Int): List<Char> {
        count += n
        return (1..n).map { iter.next() }
    } 
}

fun List<Char>.binaryToInt() = joinToString("").toInt(radix = 2)
fun List<Char>.binaryToLong() = joinToString("").toLong(radix = 2)

fun parsePacket(iter: Iterator<Char>): Packet {
    val scanner = PacketScanner(iter)
    val version = scanner.take(3).binaryToInt()
    val typeId = scanner.take(3).binaryToInt()
    if (typeId == 4) {
        val encoded = mutableListOf<Char>()
        do {
            val partial = scanner.take(5)
            encoded += partial.drop(1)
         } while (partial.first() == '1')
         return Literal(version, typeId, scanner.count, encoded.binaryToLong())
    } else {
        if (scanner.take(1).first() == '0') {
            val length = scanner.take(15).binaryToInt()
            val subpackets = mutableListOf<Packet>() 
            var remaining = length
            while (remaining > 0) {
                subpackets.add(parsePacket(iter))
                remaining -= subpackets.last().length
            }
            return Operator(version, typeId, scanner.count + length, subpackets)
        } else {
            val subpacketCount = scanner.take(11).binaryToInt()
            val subpackets = (1..subpacketCount).map { parsePacket(iter) }
            return Operator(version, typeId, scanner.count + subpackets.sumOf { it.length }, subpackets)
        }
    }
}

fun Char.hexToBinaryString() = Integer.toBinaryString((1 shl 4) or digitToInt(radix = 16)).drop(1)

val input = java.io.File(args[0]).readLines().single()
val binary = input.map { it.hexToBinaryString() }.joinToString("").toList()
val packet = parsePacket(binary.iterator())

fun sumVersions(packet: Packet): Int {
    return when (packet) {
        is Literal -> packet.version
        is Operator -> packet.version + packet.subpackets.map { sumVersions(it) }.sum()
        else -> error("")
    }
}
println(sumVersions(packet))

fun applyOperators(packet: Packet): Long {
    return when (packet) {
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
}
println(applyOperators(packet))
