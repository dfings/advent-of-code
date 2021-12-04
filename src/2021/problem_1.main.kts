#!/usr/bin/env kotlin

import java.io.File

fun Boolean.toInt() = if (this) 1 else 0
val data = File(args[0]).readLines().map { it.toInt() }
println(data.windowed(2) { (it[0] < it[1]).toInt() }.sum())
println(data.windowed(4) { (it.dropLast(1).sum() < it.drop(1).sum()).toInt() }.sum())
