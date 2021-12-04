#!/usr/bin/env kotlin

import java.io.File

val data = File(args[0]).readLines().map { it.toInt() }
println(data.windowed(2) { if (it[0] < it[1]) 1 else 0}.sum())
