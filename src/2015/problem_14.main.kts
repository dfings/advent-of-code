#!/usr/bin/env kotlin

import kotlin.math.min

data class Reindeer(val speed: Int, val time: Int, val rest: Int) {
    val block = time + rest
    fun distance(elapsed: Int) = speed * ((elapsed / block) * time + min(elapsed.rem(block), time))
}

val pattern = Regex("""(\d+).*for (\d+).*for (\d+)""")
fun String.parse(): Reindeer {
    val (speed, time, rest) = pattern.find(this)!!.destructured
    return Reindeer(speed.toInt(), time.toInt(), rest.toInt())
}

val lines = java.io.File(args[0]).readLines()
val reindeer = lines.map { it.parse() }
println(reindeer.maxOf { it.distance(2503) })

val scores = IntArray(reindeer.size)
for (i in 1..2503) {
    val distances = reindeer.map { it.distance(i) }
    val best = distances.max()
    for (i in distances.indices) {
        if (distances[i] == best) scores[i]++
    }
}
println(scores.max())
