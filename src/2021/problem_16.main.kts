#!/usr/bin/env kotlin

// Utility
fun <T> Iterator<T>.take(n: Int) = (1..n).map { next() }
fun List<Char>.binaryToInt() = joinToString("").toInt(radix = 2)
fun List<Char>.binaryToLong() = joinToString("").toLong(radix = 2)
fun Char.hexToBinaryString() = Integer.toBinaryString(digitToInt(radix = 16)).padStart(4, '0')

// AST definition
sealed interface Packet {
    fun versionSum(): Int
    fun evaluate(): Long
    fun render(): String
}

data class Literal(val version: Int, val value: Long) : Packet {
    override fun versionSum() = version
    override fun evaluate() = value
    override fun render() = "$value"
}

data class Operator(val version: Int, val typeId: Int, val packets: List<Packet>) : Packet {
    override fun versionSum() = version + packets.sumOf(Packet::versionSum)

    override fun evaluate(): Long {
        val results = packets.map { it.evaluate() }
        return when (typeId) {
            0 -> results.sum()
            1 -> results.reduce(Long::times)
            2 -> results.minOf { it }
            3 -> results.maxOf { it }
            5 -> if (results[0] > results[1]) 1 else 0
            6 -> if (results[0] < results[1]) 1 else 0
            7 -> if (results[0] == results[1]) 1 else 0
            else -> error("Bad typeId: $typeId")
        }
    }

    override fun render(): String {
        val results = packets.map { it.render() }.joinToString(" ")
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
        return "($op $results)"
    }
}

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
    val iter = take(length).iterator()
    val packets = mutableListOf<Packet>() 
    while (iter.hasNext()) {
        packets.add(iter.parsePacket())
    }
    return Operator(version, typeId, packets)
}

fun Iterator<Char>.parsePacketDelimitedOperator(version: Int, typeId: Int): Operator {
    val packetCount = take(11).binaryToInt()
    val packets = (1..packetCount).map { parsePacket() }
    return Operator(version, typeId, packets)
}

val input = java.io.File(args[0]).readLines().single()
val binary = input.map { it.hexToBinaryString() }.joinToString("").toList()
val packet = binary.iterator().parsePacket()

println(packet.versionSum())
println(packet.evaluate())
println(packet.render())
println(packet)
