#!/usr/bin/env kotlin

import kotlin.text.isDigit

class Node {
    var value = -1
    var right: Node? = null
    var left: Node? = null
    var parent: Node? = null

    fun left() = left!!
    fun right() = right!!

    fun isPair() = left != null
    fun depth(): Int = if (parent == null) 0 else 1 + parent!!.depth()
    fun magnitude(): Int = if (isPair()) 3 * left().magnitude() + 2 * right().magnitude() else value
    override fun toString() = if (isPair()) "[$left,$right]" else "$value"

    companion object {
        fun inner(left: Node, right: Node) = Node().apply {
            this.left = left
            this.right = right
            left.parent = this
            right.parent = this
        }
        
        fun leaf(value: Int) = Node().apply { this.value = value }
    }
}

fun Node.inOrderList(): List<Node> = 
    if (isPair()) {
        left().inOrderList() + listOf(this) + right().inOrderList()
    } else {
        listOf(this)
    }

fun String.parseNode() = iterator().parseNode()
fun Iterator<Char>.parseNode(): Node {
    var next: Char = next()
    return when {
        next.isDigit() -> {
            val literal = mutableListOf<Char>()
            while (next.isDigit()) {                
                literal += next
                next = next()
            }
            Node.leaf(literal.joinToString("").toInt())
        }
        next == '[' -> Node.inner(parseNode(), parseNode())
        else -> parseNode()
    }
}

fun add(left: Node, right: Node): Node {
    assert(left.parent == null && right.parent == null)
    return Node.inner(left, right).also { it.reduce() }
}

fun Node.reduce() {
    while (true) {
        val nodes = inOrderList()
        var index = nodes.indexOfFirst { it.isPair() && it.depth() >= 4 }
        if (index != -1) {
            val node = nodes[index]
            nodes.subList(0, index - 1).lastOrNull { !it.isPair() }?.let { it.value += node.left().value }
            nodes.subList(index + 2, nodes.size).firstOrNull { !it.isPair() }?.let { it.value += node.right().value }
            node.left = null
            node.right = null
            node.value = 0
            continue
        }
        index = nodes.indexOfFirst { !it.isPair() && it.value >= 10 }
        if (index != -1) {
            val node = nodes[index]
            node.left = Node.leaf(node.value / 2).also { it.parent = node }
            node.right = Node.leaf((node.value + 1) / 2).also { it.parent = node }
            node.value = -1
            continue
        }
        break
    }
}

val lines = java.io.File(args[0]).readLines()
val nodes = lines.map { it.parseNode() }
println(nodes.reduce(::add).magnitude())

fun <T> Iterable<T>.cartesianProduct() = flatMap { i -> map { j -> i to j } }

val maxMagnitude =
    lines.indices.cartesianProduct().filter { (i, j) -> i != j }.map { (i, j) ->
        add(lines[i].parseNode(), lines[j].parseNode()).magnitude()
    }.maxOf { it }
println(maxMagnitude)
