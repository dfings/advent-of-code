#!/usr/bin/env kotlin

fun factors(value: Int) = sequence<Int> {
    var i = 1
    while (i * i <= value) {
        if (value % i == 0) {
            yield(i)
            if (value / i != i) yield(value / i)
        }
        i++
    }
}

fun presents(value: Int) = factors(value).sumOf { it * 10 }
fun presents2(value: Int) = factors(value).filter { value / it <= 50 }.sumOf { it * 11 }

val input = java.io.File(args[0]).readLines().single().toInt()

var i = 1
while (presents(i) < input) i++
println(i)
while (presents2(i) < input) i++
println(i)