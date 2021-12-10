#!/usr/bin/env kotlin

val input = java.io.File(args[0]).readLines().single().toList()

var floor = 0
var basement = -1
input.forEachIndexed { index, value ->
  floor += if (value == '(') 1 else -1
  if (floor == -1 && basement == -1) {
      basement = index + 1
  } 
}
println(floor)
println(basement)
