#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val rules = lines.map { it.split(" -> ").let { it[1] to it[0] } }.toMap()

val evalCache = mutableMapOf<String, Int>()
fun eval(name: String): Int = evalCache.getOrPut(name) {
    val rule = rules[name] ?: "$name"
    0xFFFF and when {
        rule.none { it.isWhitespace() } -> rule.toIntOrNull() ?: eval(rule)
        rule.startsWith("NOT") -> eval(rule.removePrefix("NOT ")).inv()
        else -> {
            val (a, op, b) = rule.split(" ")
            when (op) {
                "AND" -> eval(a) and eval(b)
                "OR" -> eval(a) or eval(b)
                "LSHIFT" -> eval(a) shl b.toInt()
                "RSHIFT" -> eval(a) shr b.toInt()
                else -> throw IllegalArgumentException("$rule -> $name")
            }
        }
    }
}

val a = eval("a")
println(a)
evalCache.clear()
evalCache.put("b", a)
println(eval("a"))
