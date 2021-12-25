#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

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
val all = HashSet(fish.keys)

fun step(): Boolean {
    val toMoveEast = eastUnblocked.toList()
    all.removeAll(toMoveEast)
    eastUnblocked.clear()
    toMoveEast.forEach {
        it.west().let { if (eastBlocked.remove(it)) eastUnblocked.add(it) }
        it.north().let { if (southBlocked.remove(it)) southUnblocked.add(it) }
        it.east().let { 
            if (southUnblocked.remove(it.north())) southBlocked.add(it.north()) 
            if (it.east() in all) eastBlocked.add(it) else eastUnblocked.add(it)
            all.add(it)
        }
    }

    val toMoveSouth = southUnblocked.toList()
    all.removeAll(toMoveSouth)
    southUnblocked.clear()
    toMoveSouth.forEach {
        it.west().let { if (eastBlocked.remove(it)) eastUnblocked.add(it) }
        it.north().let { if (southBlocked.remove(it)) southUnblocked.add(it) }
        it.south().let { 
            if (eastUnblocked.remove(it.west())) eastBlocked.add(it.west()) 
            if (it.south() in all) southBlocked.add(it) else southUnblocked.add(it)
            all.add(it)
        }
    }

    return toMoveEast.size > 0 || toMoveSouth.size > 0
}

var stepCount = 0
while (step()) { stepCount ++ }
println(stepCount + 1)
