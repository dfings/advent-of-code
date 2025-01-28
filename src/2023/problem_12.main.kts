#!/usr/bin/env kotlin

val cache = mutableMapOf<Pair<String, List<Int>>, Long>()
fun countValid(record: String, spec: List<Int>): Long = cache.getOrPut(record to spec) {
     when {
        record.isEmpty() -> if (spec.isEmpty()) 1 else 0
        record[0] == '.' -> countValid(record.dropWhile { it == '.' }, spec)
        record[0] == '?' -> countValid(record.drop(1), spec) + countValid("#" + record.drop(1), spec)
        record[0] == '#' -> when {
            spec.isEmpty() -> 0
            record.take(spec[0]).let { it.length < spec[0] || it.any { it == '.' } } -> 0
            spec[0] == record.length -> if (spec.size == 1) 1 else 0
            record[spec[0]] == '#' -> 0
            else -> countValid(record.drop(spec[0] + 1), spec.drop(1))
        }
        else -> throw IllegalStateException()
    }
}

fun parse(input: String) = input.split(" ").let { (r, s) -> 
    r to s.split(",").map { it.toInt() }
}

fun parse2(input: String) = input.split(" ").let { (r, s) -> 
    List(5) { r }.joinToString("?") to 
    List(5) { s }.joinToString(",").split(",").map { it.toInt() }
}

fun solve(lines: List<String>) {
    println(lines.map { parse(it) }.sumOf { (record, spec) -> countValid(record, spec) })
    println(lines.map { parse2(it) }.sumOf { (record, spec) -> countValid(record, spec) })
}

solve(java.io.File(args[0]).readLines())