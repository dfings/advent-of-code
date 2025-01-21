#!/usr/bin/env kotlin

import kotlin.math.max

data class State(
    val bossHp: Int,
    val bossDamage: Int,
    val playerHp: Int,
    val playerMana: Int,
    val playerManaSpent: Int = 0,
    val playerArmor: Int = 0,
    val shieldTimer: Int = 0,
    val poisonTimer: Int = 0,
    val rechargeTimer: Int = 0,
)

fun State.upkeep() = copy(
    bossHp = if (poisonTimer > 0) bossHp - 3 else bossHp,
    playerArmor = if (shieldTimer > 1) 7 else 0,
    playerMana = if (rechargeTimer > 0) playerMana + 101 else playerMana,
    shieldTimer = max(0, shieldTimer - 1),
    poisonTimer = max(0, poisonTimer - 1),
    rechargeTimer = max(0, rechargeTimer - 1),
)

fun State.playerTurn() = buildList<State> {
    // Magic Missle
    if (playerMana >= 53) {
        add(copy(
            bossHp = bossHp - 4, 
            playerMana = playerMana - 53,
            playerManaSpent = playerManaSpent + 53,
        ))
    }
    // Drain
    if (playerMana >= 73) {
        add(copy(
            bossHp = bossHp - 2,
            playerHp = playerHp + 2,
            playerMana = playerMana - 73,
            playerManaSpent = playerManaSpent + 73,
        ))
    }
    // Shield
    if (playerMana >= 113 && shieldTimer == 0) {
        add(copy(
            playerMana = playerMana - 113,
            playerManaSpent = playerManaSpent + 113,
            playerArmor = playerArmor + 7,
            shieldTimer = 6,
        ))
    }
    // Poison
    if (playerMana >= 173 && poisonTimer == 0) {
        add(copy(
            playerMana = playerMana - 173,
            playerManaSpent = playerManaSpent + 173,
            poisonTimer = 6,
        ))
    }
    // Recharge
    if (playerMana >= 229 && rechargeTimer == 0) {
        add(copy(
            playerMana = playerMana - 229,
            playerManaSpent = playerManaSpent + 229,
            rechargeTimer = 5,
        ))
    }
}
    
fun State.bossTurn() = copy(
    playerHp = playerHp - max(0, bossDamage - playerArmor)
)

fun State.hardMode() = copy(
    playerHp = playerHp - 1
)

fun findMinManaToWin(start: State, hardMode: Boolean = false): Int {
    val frontier = java.util.PriorityQueue<State> { a, b -> 
        a.playerManaSpent.compareTo(b.playerManaSpent) 
    }
    frontier.add(start)
    while (!frontier.isEmpty()) {
        val current = frontier.poll().let { if (hardMode) it.hardMode() else it }
        if (current.playerHp <= 0) continue
        val playerUpkeep = current.upkeep()
        if (playerUpkeep.bossHp <= 0) {
            return playerUpkeep.playerManaSpent
        }
        for (playerTurn in playerUpkeep.playerTurn()) {
            val bossUpkeep = playerTurn.upkeep()
            if (bossUpkeep.bossHp <= 0) {
                return bossUpkeep.playerManaSpent
            }
            frontier.add(bossUpkeep.bossTurn())
        }
    }
    return -1
}

val lines = java.io.File(args[0]).readLines()
val bossHp = lines[0].removePrefix("Hit Points: ").toInt()
val bossDamage = lines[1].removePrefix("Damage: ").toInt()
val initialState = State(bossHp, bossDamage, 50, 500)
println(findMinManaToWin(initialState))
println(findMinManaToWin(initialState, hardMode = true))
