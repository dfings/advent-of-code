#!/usr/bin/env kotlin

val chipPattern = Regex("""(\w+)-compatible microchip""")
val generatorPattern = Regex("""(\w+) generator""")

data class Floor(val chips: Set<String>, val generators: Set<String>)
fun Floor.isValid() = generators.isEmpty() || generators.containsAll(chips)
fun String.toFloor() = Floor(
    chipPattern.findAll(this).map { it.groupValues[1] }.toSet(),
    generatorPattern.findAll(this).map { it.groupValues[1] }.toSet(),
)

data class State(val elevator: Int, val floors: List<Floor>)
fun State.isValid() = elevator in 0..3 && floors.all { it.isValid() }
fun State.move(elevatorDelta: Int, chips: Set<String>, generators: Set<String>) = State(
    elevator + elevatorDelta, 
    floors.mapIndexed { i, it -> when(i) {
        elevator -> Floor(it.chips - chips, it.generators - generators)
        elevator + elevatorDelta -> Floor(it.chips + chips, it.generators + generators)
        else -> it
    }},
)

fun State.neighbors(): List<State> = buildList {
    val currentFloor = floors[elevator]
    for (elevatorDelta in -1..1 step 2) {
        if (elevator + elevatorDelta !in 0..3) continue
        for (chip in currentFloor.chips) {
            add(move(elevatorDelta, setOf(chip), emptySet()))
            for (chip2 in currentFloor.chips.filter { chip < it }) {
                add(move(elevatorDelta, setOf(chip, chip2), emptySet()))
            }
            for (generator in currentFloor.generators) {
                add(move(elevatorDelta, setOf(chip), setOf(generator)))
            }
        }
        for (generator in currentFloor.generators) {
            add(move(elevatorDelta, emptySet(), setOf(generator)))
            for (generator2 in currentFloor.generators.filter { generator < it }) {
                add(move(elevatorDelta, emptySet(), setOf(generator, generator2)))
            }
        }
    }
}.filter { it.isValid() }

data class StateClass(val elevator: Int, val floorPattern: List<Pair<Int, Int>>, val floorOffests: List<List<Int>>)
fun State.stateClass() : StateClass {
    val floorPattern = floors.map { it.chips.size to it.generators.size }
    val generatorOffsets = floors.flatMapIndexed { i, floor -> floor.generators.map { it to i } }.toMap()
    val floorOffsets = floors.map { it.chips.mapIndexed { i, it -> generatorOffsets.getValue(it) - i }.sorted() }
    return StateClass(elevator, floorPattern, floorOffsets)
}

fun findShortestPathLength(initialState: State): Int {
    val emptyFloor = Floor(emptySet(), emptySet())
    val fullFloor = initialState.floors.run { 
        Floor(flatMap { it.chips}.toSet(), flatMap { it.generators }.toSet()) 
    }
    val endState = State(3, listOf(emptyFloor, emptyFloor, emptyFloor, fullFloor))
    val minCost = mutableMapOf(initialState.stateClass() to 0)
    val frontier = mutableSetOf(initialState to 0)
    while (!frontier.isEmpty()) {
        val (state, cost) = frontier.minBy { it.second }
        frontier.remove(state to cost)
        if (state == endState) return cost
        for (n in state.neighbors()) {
            val stateClass = n.stateClass()
            val oldCost = minCost[stateClass] ?: Int.MAX_VALUE
            val newCost = cost + 1
            if (newCost < oldCost) {  
                minCost[stateClass] = newCost
                frontier.add(n to newCost)
            }
        }
    }
    return -1
}

val lines = java.io.File(args[0]).readLines()
val initialState = State(0, lines.map { it.toFloor() })
println(findShortestPathLength(initialState))

val newTypes = setOf("elerium", "dilithium")
val newFloor = initialState.floors[0].run { Floor(chips + newTypes, generators + newTypes) }
val initialState2 = initialState.copy(floors = listOf(newFloor) + initialState.floors.drop(1))
println(findShortestPathLength(initialState2))
