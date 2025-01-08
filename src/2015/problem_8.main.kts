#!/usr/bin/env kotlin

fun String.memoryLength() = replace(Regex("""(\\\\|\\x\w\w|\\\")"""), "|").length - 2
fun String.encodedLength() = replace(Regex("""(\"|\\)"""), "||").length + 2

val lines = java.io.File(args[0]).readLines()
println(lines.sumOf { it.length - it.memoryLength() })
println(lines.sumOf { it.encodedLength() - it.length } )
