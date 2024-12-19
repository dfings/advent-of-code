#!/usr/bin/env kotlin

class TowelSet(val towels: List<String>) {
    fun canMakePatternRecursive(pattern: String, partial: String): Boolean = when {
        partial == pattern -> true
        !pattern.startsWith(partial) -> false
        partial.length >= pattern.length -> false
        else -> towels.any { canMakePatternRecursive(pattern, partial + it) } 
    }

    fun canMakePattern(pattern: String) = canMakePatternRecursive(pattern, "")
}


val lines = java.io.File(args[0]).readLines()
val towels = lines[0].split(", ")
val patterns = lines.drop(2)
println(towels)
println(patterns)

val towelSet = TowelSet(towels)
println(patterns.count { towelSet.canMakePattern(it) })
