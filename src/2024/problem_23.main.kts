#!/usr/bin/env kotlin

val lines = java.io.File(args[0]).readLines()
val pairs = lines.map { it.split("-").toSet() }.toSet()

val links = pairs.flatMap { listOf(it.toList(), it.toList().reversed()) }
    .groupBy({ it[0] }, { it[1 ]})
    .mapValues { it.value.toSet() }

fun Set<String>.intersectLinks() = map { links.getValue(it) }.reduce(Set<String>::intersect)
fun Set<Set<String>>.next() = flatMap { clique ->
    clique.intersectLinks().map { clique + it }
}.toSet()

var triples = pairs.next()
println(triples.count { clique -> clique.any { it.startsWith("t") } })

var maximalCliques = triples
while (maximalCliques.size > 1) {
    maximalCliques = maximalCliques.next()
}
println(maximalCliques.single().sorted().joinToString(","))

class BronKerboschSolver<V>(private val graph: Map<V, Set<V>>) {
    private var result = emptyList<V>()

    fun solve(): List<V> {
        bronKerbosch(emptyList(), graph.keys.toList(), emptyList())
        return result
    }

    private fun bronKerbosch(r: List<V>, p: List<V>, x: List<V>) {
        if (p.isEmpty() && x.isEmpty()) {
            if (result.size < r.size) result = r
            return
        }
        val pivot = p.firstOrNull() ?: x.first()
        val p1 = p.toMutableList()
        val x1 = x.toMutableList()
        for (v in (p - graph.getValue(pivot))) {
            val es = graph.getValue(v)
            bronKerbosch(r + v, p1.filter { it in es }, x1.filter { it in es })
            p1 -= v
            x1 += v
        }
    }
}

val solver = BronKerboschSolver(links)
println(solver.solve().sorted().joinToString(","))
