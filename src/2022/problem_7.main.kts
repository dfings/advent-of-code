#!/usr/bin/env kotlin

class Dir(val parent: Dir? = null) {
    val dirs: MutableList<Dir> = mutableListOf()
    var fileSize: Int = 0
    val size: Int by lazy {
        dirs.sumBy { it.size } + fileSize
    }
}

val lines = java.io.File(args[0]).readLines().drop(1)
var cwd = Dir()
val allDirs = mutableListOf<Dir>(cwd)
for (line in lines) {
  when {
    line[0].isDigit() -> cwd.fileSize += line.split(" ").first().toInt()
    line.startsWith("$ cd ..") -> cwd = cwd.parent!!
    line.startsWith("$ cd ") -> Dir(cwd).apply {
        cwd.dirs.add(this)
        allDirs.add(this)
        cwd = this
    }
  }
}

println(allDirs.filter { it.size <= 100000 }.sumBy { it.size })

val amountToDelete = 30000000 - (70000000 - allDirs.first().size)
println(allDirs.sortedBy { it.size }.find { it.size >= amountToDelete }?.size)
