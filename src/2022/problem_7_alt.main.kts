#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines().drop(1)
var cwd = mutableListOf("/")
val dirs = mutableMapOf<String, Int>()
for (line in lines) {
    when {
        line[0].isDigit() -> {
            val size = line.split(" ").first().toInt()
            (1..cwd.size).forEach {
                val name = cwd.take(it).joinToString("")
                dirs[name] = (dirs[name] ?: 0) + size 
            }
        }
        line.startsWith("$ cd ..") -> cwd.removeLast()
        line.startsWith("$ cd ") -> cwd.add("${line.drop(5)}/")
    }
}

println(dirs.values.filter { it <= 100000 }.sum())

val amountToDelete = 30000000 - (70000000 - dirs.getValue("/"))
println(dirs.values.sorted().find { it >= amountToDelete })
