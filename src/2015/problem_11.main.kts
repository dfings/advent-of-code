#!/usr/bin/env kotlin

val bad = "iol"
val pairRule = Regex("""(\w)\1.*(\w)\2""")

fun String.isValid(): Boolean = 
    none { it in bad } &&
    windowed(3).any { it[0] == it[1] - 1 && it[1] == it[2] - 1 } &&
    pairRule.containsMatchIn(this)

fun Char.encodeOffset(): Int = if (this < 'a' + 10) 'a' - '0' else 10
fun String.encode() = map { it - it.encodeOffset() }.joinToString("").toLong(26)

fun Char.decodeOffset(): Int = if (this < 'a') 'a' - '0' else 10
fun Long.decode() = toString(26).map { it + it.decodeOffset() }.joinToString("")

val input = java.io.File(args[0]).readLines().single()

var password = input.encode()
var count = 0
while (true) {
    if (password.decode().isValid()) {
        println(password.decode())
        count++
    }
    password += 1
    if (count == 2) break
}
