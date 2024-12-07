#!/usr/bin/env kotlin

data class Equation(val total: Long, val values: List<Long>)
fun String.toEquation() = Equation(
    substringBefore(":").toLong(), 
    substringAfter(": ").split(" ").map { it.toLong() }
)

enum class Operator { ADD, MULTIPLY, CONCAT }
operator fun Operator.invoke(a: Long, b: Long) = when (this) {
    Operator.ADD -> a + b
    Operator.MULTIPLY -> a * b
    Operator.CONCAT -> "$a$b".toLong()
}

fun Equation.checkRecusive(subtotal: Long, index: Int, op: Operator, validOps: List<Operator>): Boolean {
    val newSubtotal = op(subtotal, values[index])
    if (newSubtotal > total) return false
    if (index == values.lastIndex) return newSubtotal == total
    return validOps.any { newOp -> checkRecusive(newSubtotal, index + 1, newOp, validOps) }
}

fun Equation.check(validOps: List<Operator>) =
    validOps.any { op -> checkRecusive(values[0], 1, op, validOps) }

val lines = java.io.File(args[0]).readLines()
val equations = lines.map { it.toEquation() }
println(equations.filter { it.check(Operator.entries.take(2)) }.sumOf { it.total })
println(equations.filter { it.check(Operator.entries) }.sumOf { it.total })

// This is the first way I solved the problem.
fun Equation.checkOriginal(): Boolean {
    val combos = (2 shl values.size) - 1
    for (i in 0..combos) {
        var result = values[0]
        for (j in 1..values.lastIndex) {
            when ((i shr j) and 1) {
                0 -> result += values[j]
                1 -> result *= values[j]
            }
            if (result > total) break
        }
        if (total == result) return true
    }
    return false
}