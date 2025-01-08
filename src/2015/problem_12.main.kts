#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn(".org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.8.0")

import kotlinx.serialization.json.*

val input = java.io.File(args[0]).readLines().single()
val deserializedElement = Json{}.parseToJsonElement(input)

val red = JsonPrimitive("red")
fun JsonElement.sum(filterRed: Boolean): Int = when (this) {
    is JsonNull -> 0
    is JsonPrimitive -> intOrNull ?: 0
    is JsonArray -> sumOf { it.sum(filterRed) }
    is JsonObject -> if (filterRed && containsValue(red)) 0 else values.sumOf { it.sum(filterRed) }
}

println(deserializedElement.sum(false))
println(deserializedElement.sum(true))
