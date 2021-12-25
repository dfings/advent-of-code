#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val start = System.nanoTime()

data class Point(val x: Int, val y: Int)
val max = Point(lines[0].lastIndex, lines.lastIndex)
fun Point.west() = copy(x = if (x == 0) max.x else x - 1)
fun Point.east() = copy(x = if (x == max.x) 0 else x + 1)
fun Point.north() = copy(y = if (y == 0) max.y else y - 1)
fun Point.south() = copy(y = if (y == max.y) 0 else y + 1)

inline fun <K, V> Map<K, V>.toKeySet(filter: (Map.Entry<K, V>) -> Boolean) =
    HashSet(mapNotNull { if (filter(it)) it.key else null })

val fish = lines.flatMapIndexed { y, line ->
    line.mapIndexedNotNull { x, char ->
        when (char) {
            '.' -> null
            else -> Point(x, y) to char
        }
    }
}.toMap()

val eastUnblocked = fish.toKeySet { it.value == '>' &&  it.key.east() !in fish }
val eastBlocked = fish.toKeySet { it.value == '>' &&  it.key.east() in fish }
val southUnblocked = fish.toKeySet { it.value == 'v' &&  it.key.south() !in fish }
val southBlocked = fish.toKeySet { it.value == 'v' &&  it.key.south() in fish }

fun Point.maybeUnblockEastFish() =  west().let { if (eastBlocked.remove(it)) eastUnblocked.add(it) }
fun Point.maybeBlockEastFish() = west().let { if (eastUnblocked.remove(it)) eastBlocked.add(it) }
fun Point.maybeUnblockSouthFish() = north().let { if (southBlocked.remove(it)) southUnblocked.add(it) }
fun Point.maybeBlockSouthFish() = north().let { if (southUnblocked.remove(it)) southBlocked.add(it) }

fun Point.hasFish() = this in eastUnblocked || this in eastBlocked || this in southUnblocked || this in southBlocked
fun Point.finishEastMove() = if (east().hasFish()) eastBlocked.add(this) else eastUnblocked.add(this)
fun Point.finishSouthMove() = if (south().hasFish()) southBlocked.add(this) else southUnblocked.add(this)

var stepCount = 0
while (true) {
    stepCount++

    val toMoveEast = eastUnblocked.toList()
    eastUnblocked.clear()
    toMoveEast.forEach {
        it.maybeUnblockEastFish()
        it.maybeUnblockSouthFish()
        it.east().let { 
            it.maybeBlockSouthFish()
            it.finishEastMove()            
        }
    }

    val toMoveSouth = southUnblocked.toList()
    southUnblocked.clear()
    toMoveSouth.forEach {
        it.maybeUnblockEastFish()
        it.maybeUnblockSouthFish()
        it.south().let { 
            it.maybeBlockEastFish()
            it.finishSouthMove()
        }
    }

    if (toMoveEast.isEmpty() && toMoveSouth.isEmpty()) break
}
println(stepCount)
println("Runtime: ${(System.nanoTime() - start)/1_000_000}ms")
