#!/usr/bin/env kotlin

import kotlin.math.pow

val lines = java.io.File(args[0]).readLines()

val spaces = Regex("\\s+")
val total = lines.map { 
    val (picks, winners) = it.split(':', '|').drop(1).map { it.trim().split(spaces).map { it.toInt()} }
    2.0.pow((picks intersect winners).size - 1.0).toInt()
}.sum()

println(total)
