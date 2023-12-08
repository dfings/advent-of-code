#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

val regex = Regex("""(...) = \((...), (...)\)""")

fun String.parse(): Pair<String, List<String>> {
    val (node, left, right) = regex.find(this)!!.destructured
    return node to listOf(left, right)
}

val directions = lines[0]
val map = lines.drop(2).map { it.parse() }.toMap()

var node = "AAA"
var step = 0
while (node != "ZZZ") {
    val d = directions[step % directions.length]
    node = map.getValue(node).get(if (d == 'L') 0 else 1)
    println(node)
    step++
}
println(step)
