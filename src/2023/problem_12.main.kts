#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()

fun List<Int>.asRegex() = Regex(buildString {
    append("""\.*""")
    append(this@asRegex.map { "#{$it}" }.joinToString("""\.+"""))
    append("""\.*""")
})

var totalCount = 0
for (line in lines) {
    val (records, spec) = line.split(" ").let { (r, s) -> r to s.split(",").map { it.toInt() }.asRegex() }
    val recordList = records.toMutableList()
    val unknowns = records.mapIndexedNotNull { i, it -> if (it == '?') i else null }
    var count = 0
    for (bits in 0..<(1 shl unknowns.size)) {
        for (i in 0..unknowns.lastIndex) {
            recordList[unknowns[i]] = if (bits and (1 shl i) > 0 ) '#' else '.'
        }
        val temp = recordList.joinToString("")
        if (spec.matches(temp)) {
            count++
        }
    }
    println(count)
    totalCount += count
}
println(totalCount)
