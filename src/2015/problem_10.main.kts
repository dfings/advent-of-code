#!/usr/bin/env kotlin

fun List<Int>.lookSay(): List<Int> {
    var look = this
    val say = mutableListOf<Int>()
    while (!look.isEmpty()) {
        val end = look.indexOfFirst { it != look[0] }
        val took = if (end == -1) look.size else end
        say.add(took)
        say.add(look[0])
        look = look.subList(took, look.size)
    }
    return say
}

val input = java.io.File(args[0]).readLines().single()
var value = input.map { it - '0' }
repeat (40) {
    value = value.lookSay()
}
println(value.size)
repeat (10) {
    value = value.lookSay()
}
println(value.size)
