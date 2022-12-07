#!/usr/bin/env kotlin

class Dir(val name: String, val parent: Dir? = null) {
    val dirs: MutableList<Dir> = mutableListOf()
    var fileSize: Int = 0
    val size: Int by lazy {
        dirs.sumBy { it.size } + fileSize
    }
}

val lines = java.io.File(args[0]).readLines().drop(1)
val root = Dir("/")
var cwd = root
val allDirs = mutableListOf<Dir>(root)
for (line in lines) {
  when {
    line.startsWith("$ ls") -> {}
    line.startsWith("dir ") -> Dir(line.drop(4), cwd).apply {
        cwd.dirs.add(this)
        allDirs.add(this)
    }
    line[0].isDigit() -> cwd.fileSize += line.split(" ").first().toInt()
    line.startsWith("$ cd ..") -> cwd = cwd.parent!!
    line.startsWith("$ cd ") -> cwd = cwd.dirs.find { it.name == line.drop(5) }!!
  }
}

println(allDirs.filter { it.size <= 100000 }.sumBy { it.size })

val amountToDelete = 30000000 - (70000000 - root.size)
println(allDirs.sortedBy { it.size }.find { it.size >= amountToDelete }?.size)
