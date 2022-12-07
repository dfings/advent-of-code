#!/usr/bin/env kotlin

class Dir(val name: String, parentDir: Dir? = null) {
    val parent: Dir = parentDir ?: this
    val dirs: MutableList<Dir> = mutableListOf()
    val files: MutableList<File> = mutableListOf()
    val size: Int by lazy {
        dirs.sumBy { it.size } + files.sumBy { it.size }
    }
}

class File(val name: String, val parent: Dir, val size: Int)

val lines = java.io.File(args[0]).readLines().drop(1)
val root = Dir("/")
var cwd = root
val allDirs = mutableListOf<Dir>(root)
for (line in lines) {
  when {
    line.startsWith("$ ls") -> {}
    line.startsWith("dir ") -> {
        cwd.dirs.add(Dir(line.drop(4), cwd))
        allDirs.add(cwd.dirs.last())
    }
    line[0].isDigit() -> {
        val (size, name) = line.split(" ")
        cwd.files.add(File(name, cwd, size.toInt()))
    }
    line.startsWith("$ cd ..") -> cwd = cwd.parent
    line.startsWith("$ cd ") -> cwd = cwd.dirs.find { it.name == line.drop(5) }!!
  }
}

println(allDirs.filter { it.size <= 100000 }.sumBy { it.size })

val amountToDelete = 30000000 - (70000000 - root.size)
println(allDirs.sortedBy { it.size }.find { it.size >= amountToDelete }?.size)
