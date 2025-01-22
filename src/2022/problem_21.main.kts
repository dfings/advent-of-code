#!/usr/bin/env kotlin

sealed interface Monkey {
    val name: String
}

data class ConstantMonkey(override val name: String, var output: Long) : Monkey
data class OperatorMonkey(
    override val name: String, 
    val monkey1: String, 
    val monkey2: String, 
    val op: Char
) : Monkey

fun parse(input: String): Monkey {
    val name = input.take(4)
    if (input[6].isDigit()) {
        return ConstantMonkey(name, input.drop(6).toLong())
    } else {
        return OperatorMonkey(name, input.substring(6, 10), input.substring(13, 17), input[11])
    }
}

val lines = java.io.File(args[0]).readLines()

val monkeys = lines.map { parse(it) }.associateBy { it.name }
fun Map<String, Monkey>.eval(name: String): Long {
    val monkey = getValue(name)
    return when (monkey) {
        is ConstantMonkey -> monkey.output
        is OperatorMonkey -> {
            val input1 = eval(monkey.monkey1)
            val input2 = eval(monkey.monkey2)
            when (monkey.op) {
                '+' -> Math.addExact(input1, input2)
                '-' -> Math.subtractExact(input1, input2)
                '*' -> Math.multiplyExact(input1, input2)
                '/' -> input1 / input2
                else -> throw IllegalStateException()
            }
        }
    }
}

println(monkeys.eval("root"))

val root = monkeys.getValue("root") as OperatorMonkey
val target = monkeys.eval(root.monkey2)
val humn = monkeys.getValue("humn") as ConstantMonkey
fun  Map<String, Monkey>.evalWithInput(input: Long): Long {
    humn.output = input
    return eval(root.monkey1)
}

fun Map<String, Monkey>.findCorrectInput(lowerInput: Long, upperInput: Long): Long {
    val lowerOutput = evalWithInput(lowerInput)
    val upperOutput = evalWithInput(upperInput)
    require(lowerOutput <= target)
    require(upperOutput >= target)
    val midpointInput = (upperInput + lowerInput) / 2
    val midpointOutput = evalWithInput(midpointInput)
    return when {
        midpointOutput == target -> if (evalWithInput(midpointInput - 1) == midpointOutput) midpointInput - 1 else midpointInput
        midpointOutput < target -> findCorrectInput(midpointInput, upperInput)
        else -> findCorrectInput(lowerInput, midpointInput)
    }
}

println(monkeys.findCorrectInput((monkeys.eval(root.monkey1) - target) / 10, 0))
