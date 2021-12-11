#!/usr/bin/env kotlin

class Octopus(val x: Int, val y: Int, var energy: Int) {
    fun shouldFlash() = energy > 9
}

class Board(val octopuses: List<List<Octopus>>) {
    val all = octopuses.flatten()
    val xMax = octopuses[0].lastIndex
    val yMax = octopuses.lastIndex

    fun step(): Int {
        all.forEach { it.energy++ }
        var pending = all.filter { it.shouldFlash() }
        val flashed = HashSet<Octopus>()
        while (!pending.isEmpty()) {
            flashed.addAll(pending)
            pending.forEach { it.neighbors().forEach { it.energy++ } }
            pending = all.filter { it.shouldFlash() && it !in flashed }
        }
        all.filter { it.shouldFlash() }.forEach { it.energy = 0 }
        return flashed.size
    }
    
    fun Octopus.neighbors() = listOf(
        octopusAt(x - 1, y - 1),  octopusAt(x, y - 1), octopusAt(x + 1, y - 1),
        octopusAt(x - 1, y),                           octopusAt(x + 1, y),
        octopusAt(x - 1, y + 1),  octopusAt(x, y + 1), octopusAt(x + 1, y + 1),
    ).filterNotNull()

    fun octopusAt(x: Int, y: Int): Octopus? =
        if (x < 0 || x > xMax || y < 0 || y > yMax) null else octopuses[y][x]
}

val board = Board(
    java.io.File(args[0]).readLines().mapIndexed { y, line ->
        line.chunked(1).mapIndexed { x, char -> 
            Octopus(x, y, char.toInt())
        } 
    }
)

var stepCount = 0
var totalFlashed = 0
var allFlashedAt = -1
while (stepCount < 100 || allFlashedAt == -1) {
    stepCount++
    val flashedCount = board.step()
    if (allFlashedAt == -1 && flashedCount == board.all.size) {
        allFlashedAt = stepCount
    }
    totalFlashed += flashedCount
    if (stepCount == 100) println("Step 100 count: $totalFlashed")
    if (allFlashedAt != -1) println("All flashed at: $stepCount")
}

