#!/usr/bin/env kotlin

class Octopus(val x: Int, val y: Int, var energy: Int) {
    val shouldFlash: Boolean get() = energy > 9
}

class Cave(val octopuses: List<List<Octopus>>) {
    val all = octopuses.flatten()
    val xMax = octopuses[0].lastIndex
    val yMax = octopuses.lastIndex

    fun start() = sequence {
        while(true) {
            yield(step())
        }
    }

    fun step(): Int {
        all.forEach { it.energy++ }
        var pending = all.filter { it.shouldFlash }
        val flashed = HashSet<Octopus>()
        while (!pending.isEmpty()) {
            flashed.addAll(pending)
            pending.forEach { it.neighbors().forEach { it.energy++ } }
            pending = all.filter { it.shouldFlash && it !in flashed }
        }
        all.filter { it.shouldFlash }.forEach { it.energy = 0 }
        return flashed.size
    }
    
    fun Octopus.neighbors() = listOfNotNull(
        octopusAt(x - 1, y - 1),  octopusAt(x, y - 1), octopusAt(x + 1, y - 1),
        octopusAt(x - 1, y),                           octopusAt(x + 1, y),
        octopusAt(x - 1, y + 1),  octopusAt(x, y + 1), octopusAt(x + 1, y + 1),
    )

    fun octopusAt(x: Int, y: Int): Octopus? =
        if (x < 0 || x > xMax || y < 0 || y > yMax) null else octopuses[y][x]
}

fun cave() = Cave(
    java.io.File(args[0]).readLines().mapIndexed { y, line ->
        line.mapIndexed { x, char -> 
            Octopus(x, y, char.digitToInt())
        } 
    }
)

println(cave().start().take(100).sum())
println(cave().run { start().takeWhile { it != all.size }.count() + 1 } )
