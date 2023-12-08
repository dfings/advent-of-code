#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun String.parse(): Pair<String, List<String>> {
    val regex = Regex("""(...) = \((...), (...)\)""")
    val (node, left, right) = regex.find(this)!!.destructured
    return node to listOf(left, right)
}

val directions = lines[0]
val map = lines.drop(2).map { it.parse() }.toMap()

fun pathLength(start: String, done: (String) -> Boolean): Long {
    var node = start
    var step = 0
    while (!done(node)) {
        val d = directions[step % directions.length]
        node = map.getValue(node).get(if (d == 'L') 0 else 1)
        step++
    }
    return step.toLong()
}
println(pathLength("AAA") { it == "ZZZ" })

tailrec fun gcd(x: Long, y: Long): Long = if (y == 0L) x else gcd(y, x % y)
fun lcm(x: Long, y: Long): Long = (x * y) / gcd(x, y)

val lengths = map.keys.filter { it.endsWith('A') }.map { pathLength(it) { it.endsWith('Z') } } 
println(lengths.fold(1L, this::lcm))
