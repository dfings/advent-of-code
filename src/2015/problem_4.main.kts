#!/usr/bin/env kotlin

import java.math.BigInteger

val md5hash = java.security.MessageDigest.getInstance("MD5")
fun md5(input: String) = 
    BigInteger(1, md5hash.digest(input.toByteArray())).toString(16).padStart(32, '0')

fun findCoin(key: String, target: String): Int {
    var i = 0
    while (true) {
        val hash = md5("$key$i")
        if (hash.startsWith(target)) {
            return i
        }
        i++
    }
}

val key = java.io.File(args[0]).readLines().single()
println(findCoin(key, "00000"))
println(findCoin(key, "000000"))
