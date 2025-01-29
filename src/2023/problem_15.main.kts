#!/usr/bin/env kotlin

fun String.hash(): Int {
    var value = 0
    for (i in indices) {
        value += get(i).toInt()
        value *= 17
        value = value.mod(256)
    }
    return value
}

val pattern = Regex("""(\w+)(-|=)(\d+)?""")
fun solve(lines: List<String>) {
    val instructions = lines[0].split(",")
    println(instructions.sumOf { it.hash() })

    val boxes = List(256) { mutableMapOf<String, String>() }
    for (instruction in instructions) {
        val (label, code, focal) = pattern.find(instruction)!!.destructured
        val index = label.hash()
        if (code == "=") {
            boxes[index].put(label, focal)
        } else {
            boxes[index].remove(label)
        }
    }
    val power = boxes.withIndex().sumOf { (i, box) -> 
        box.entries.withIndex().sumOf { (j, it) -> (i + 1) * (j + 1) * it.value.toInt() } 
    }
    println(power)
}

solve(java.io.File(args[0]).readLines())
