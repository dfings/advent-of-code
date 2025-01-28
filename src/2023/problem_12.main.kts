#!/usr/bin/env kotlin

fun String.canPlace(startIndex: Int, count: Int): Boolean {
    if (length - startIndex < count) return false
    val endIndex = startIndex + count
    return getOrElse(startIndex - 1) { '.' } != '#' &&
        getOrElse(endIndex) { '.' } != '#' &&
        (startIndex..<endIndex).all { get(it) == '#' || get(it) == '?' }
}

fun String.validStarts(startIndex: Int) = sequence<Int> {
    for (i in startIndex..lastIndex) {
        when (get(i)) {
            '?' -> yield(i)
            '#' -> {
                yield(i)
                break
            }
        }
    }
}

fun String.noSprings() = none { it == '#' }

fun countValid(record: String, spec: List<Int>): Int {
    if (spec.isEmpty()) return if (record.noSprings()) 1  else 0
    val count = spec[0]
    var total = 0
    val validStarts = record.validStarts(0).toList()
    for (i in validStarts) {
        if (record.canPlace(i, count)) {
            total += countValid(record.drop(i + count + 1), spec.subList(1, spec.size))
        }
    }
    return total
}

fun parse(input: String) = input.split(" ").let { (r, s) -> 
    r to s.split(",").map { it.toInt() }
}
fun parse2(input: String) = input.split(" ").let { (r, s) -> 
    List(5) { r }.joinToString("?") to 
    List(5) { s }.joinToString(",").split(",").map { it.toInt() }
}

fun solve(lines: List<String>) {
    var totalCount = 0
    for (line in lines) {
        val (records, spec) = parse(line)
        println()
        println("${records} ${spec.joinToString()}")
        val count = countValid(records, spec)
        println(count)
        totalCount += count
    }
    println(totalCount)
}

solve(java.io.File(args[0]).readLines())