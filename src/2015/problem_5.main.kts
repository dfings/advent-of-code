#!/usr/bin/env kotlin

val vowels = "aeiou"
val bad = setOf("ab", "cd", "pq", "xy")
fun String.isNice(): Boolean = 
    count { it in vowels } >= 3 &&
    windowed(2).any { it[0] == it[1] } &&
    windowed(2).none { it in bad }

val rule1 = Regex("""(\w\w).*\1""")
val rule2 = Regex("""(\w)\w\1""")
fun String.isNice2() = rule1.containsMatchIn(this) && rule2.containsMatchIn(this)

val lines = java.io.File(args[0]).readLines()

println(lines.count { it.isNice() })
println(lines.count { it.isNice2() })
