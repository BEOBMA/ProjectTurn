@file:Suppress("DEPRECATION")

package org.beobma.projectturn.util

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup
import org.beobma.projectturn.util.Utill.Companion.toCard
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

class Utill {
    companion object {
        fun ItemStack.toCard(): Card {
            val parts = this.itemMeta.displayName.split("|").map { it.trim() }

            val name = parts[0].substringAfter("${ChatColor.BOLD}").trim()
            val cost = parts[1].substring(1, parts[1].length - 1).toInt()
            val lore = this.itemMeta.lore as List<String>

            when {
                parts[0].substringBefore("${ChatColor.BOLD}") == "${ChatColor.WHITE}" -> {
                    return Card(name, lore, RarityType.Common, cost)
                }
                parts[0].substringBefore("${ChatColor.BOLD}") == "${ChatColor.BLUE}" -> {
                    return Card(name, lore, RarityType.Uncommon, cost)
                }
                parts[0].substringBefore("${ChatColor.BOLD}") == "${ChatColor.GREEN}" -> {
                    return Card(name, lore, RarityType.Rare, cost)
                }
                parts[0].substringBefore("${ChatColor.BOLD}") == "${ChatColor.YELLOW}" -> {
                    return Card(name, lore, RarityType.Legend, cost)
                }
            }
            return Card("오류 카드", listOf("이 카드는 존재할 수 없는 카드입니다."), RarityType.Common, 999)
        }

        fun Player.isTurn(): Boolean {
            return player!!.scoreboardTags.contains("this_Turn")
        }

        fun Entity.isTeam(): Boolean {
            return this is Player
        }

        fun Entity.isEnemy(): Boolean {
            return this !is Player
        }

        fun Int.calculatePercentage(percentage: Double): Int {
            val result = this * percentage / 100.0
            return result.roundToInt()
        }
    }

    fun shootLaser(player: Player): Entity? {
        val world = player.world

        val rayTraceResult = world.rayTraceEntities(
            player.eyeLocation, player.eyeLocation.direction, 100.0, 0.1
        ) {
            it != player
        }

        return rayTraceResult?.hitEntity
    }
}