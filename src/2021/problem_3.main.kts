#!/usr/bin/env kotlin

import java.io.File

val data = File(args[0]).readLines().map { it.toList().map(Character::getNumericValue) }

// Part 1
val bitCount = data.reduce { acc, it -> acc.zip(it, Int::plus) }
val cutoff = data.size / 2
val gamma = bitCount.map { if (it > cutoff) 1 else 0 }.joinToString("")
val epsilon = bitCount.map { if (it < cutoff) 1 else 0  }.joinToString("")
println(gamma.toInt(2) * epsilon.toInt(2))
