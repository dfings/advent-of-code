#!/usr/bin/env kotlin

import kotlin.math.max
import kotlin.math.min

data class Item(val price: Int, val damage: Int, val armor: Int)
operator fun Item.plus(other: Item) = Item(price + other.price, damage + other.damage, armor + other.armor)

val weapons = listOf(
    Item(8, 4, 0),
    Item(10, 5, 0),
    Item(25, 6, 0),
    Item(40, 7, 0),
    Item(74, 8, 0),
)

val armor = listOf(
    Item(0, 0, 0),
    Item(13, 0, 1),
    Item(31, 0, 2),
    Item(53, 0, 3),
    Item(75, 0, 4),
    Item(102, 0, 5),
)

val rings = listOf(
    Item(0, 0, 0),
    Item(25, 1, 0),
    Item(50, 2, 0),
    Item(100, 3, 0),
    Item(20, 0, 1),
    Item(40, 0, 2),
    Item(80, 0, 3),
)

val lines = java.io.File(args[0]).readLines()

val bossHp = lines[0].removePrefix("Hit Points: ").toInt()
val bossDamage = lines[1].removePrefix("Damage: ").toInt()
val bossArmor = lines[2].removePrefix("Armor: ").toInt()

fun deathTurn(hp: Int, armor: Int, damage: Int) = if (damage <= armor) Int.MAX_VALUE else 1 + (hp - 1) / (damage - armor)
fun Item.bossDeathTurn() = deathTurn(bossHp, bossArmor, damage)
fun Item.playerDeathTurn() = deathTurn(100, armor, bossDamage)

var bestPrice = Int.MAX_VALUE
var worstPrice = 0
for (w in weapons) {
    for (a in armor) {
        for (r1 in rings) {
            for (r2 in rings) {
                if (r1 !== r2 || r1.price == 0) {
                    val items = w + a + r1 + r2
                    if (items.bossDeathTurn() <= items.playerDeathTurn()) {
                        bestPrice = min(bestPrice, items.price)
                    } else {
                        worstPrice = max(worstPrice, items.price)
                    }
                }
            }
        }
    }
}

println(bestPrice)
println(worstPrice)
