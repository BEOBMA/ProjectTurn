package org.beobma.projectturn.game

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import kotlin.enums.enumEntries

data class TurnCount(
    val entity: Entity,
    var turnTime: Int,
    val tagString: String,
    val effectText: String
) {
    fun countStart() {
        val game = Info().getGame() ?: return

        game.countTimer.add(this)
        if (entity is Player) {
            entity.sendMessage("${ChatColor.BOLD}${ChatColor.GREEN}$effectText 효과가 ${turnTime}턴 동안 적용됩니다.")

        } else {
            TextManager().gameNotification("${ChatColor.BOLD}${ChatColor.RED}${game.gameEnemyStats[entity]?.name}의 $effectText 효과가 ${turnTime}턴 동안 적용됩니다.")
        }
    }

    fun countDown() {
        val game = Info().getGame() ?: return

        if (entity.isDead) {
            game.countTimer.remove(this)
            return
        }

        turnTime -= 1
        if (turnTime == 0) {
            countEnd()
            return
        }

        if (entity is Player) {
            entity.sendMessage("${ChatColor.BOLD}${ChatColor.GREEN}$effectText 효과가 ${turnTime}턴 후 제거됩니다.")

        } else {
            TextManager().gameNotification("${ChatColor.BOLD}${ChatColor.RED}${game.gameEnemyStats[entity]?.name}의 $effectText 효과가 ${turnTime}턴 후 제거됩니다.")
        }
    }

    fun countEnd() {
        val game = Info().getGame() ?: return

        if (entity.isDead) {
            game.countTimer.remove(this)
            return
        } else {
            if (entity is Player) {
                entity.sendMessage("${ChatColor.BOLD}${ChatColor.GREEN}$effectText 효과가 제거되었습니다.")

            } else {
                TextManager().gameNotification("${ChatColor.BOLD}${ChatColor.RED}${game.gameEnemyStats[entity]?.name}의 $effectText 효과가 제거되었습니다.")
            }
        }

        when (this.tagString) {
            "WolfDeadSpeedUpTag" -> {
                game.gameEnemyStats[entity]!!.relativeSpeed -= 1
            }
        }
        game.countTimer.remove(this)
    }
}