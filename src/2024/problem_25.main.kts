#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val (lockLines, keyLines) = lines.chunked(8).map { it.take(7) }.partition { it[0][0] == '#' }

val locks = lockLines.map { lines -> (0..4).map { y -> lines.map { it[y] }.lastIndexOf('#') } }
val keys = keyLines.map { lines -> (0..4).map { y -> 5 - lines.map { it[y] }.lastIndexOf('.') } }


var total = keys.sumOf { key ->
    locks.count { lock -> (0..4).all { i -> key[i] + lock[i] <= 5 }}
}
println(total)

val precomputed = IntArray(6 * 6 * 6 * 6 * 6)
for (lock in locks) {
    for (i1 in 0..5) {
        if (lock[0] + i1 > 5) break
        for (i2 in 0..5) {
            if (lock[1] + i2 > 5) break
            for (i3 in 0..5) {
                if (lock[2] + i3 > 5) break
                for (i4 in 0..5) {
                    if (lock[3] + i4 > 5) break
                    for (i5 in 0..5) {
                        if (lock[4] + i5 > 5) break
                        precomputed[i1 + 6*i2 + 6*6*i3 + 6*6*6*i4 + 6*6*6*6*i5]++
                    }
                }
            }
        }
    }
}
total = keys.sumOf { precomputed[it[0] + it[1]*6 + it[2]*6*6 + it[3]*6*6*6 + it[4]*6*6*6*6]}
println(total)
