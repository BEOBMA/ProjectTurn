@file:Suppress("DEPRECATION")

package org.beobma.projectturn.game

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.text.TextManager
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

data class TurnCount(
    val entity: Entity,
    var turnTime: Int,
    val tagString: String,
    val effectText: String
) {
    fun countStart() {
        val game = Info().getGame() ?: return

        game.countTimer.add(this)
        sendMessage("${ChatColor.BOLD}${ChatColor.GREEN}$effectText 효과가 ${turnTime}턴 동안 적용됩니다.", "${ChatColor.BOLD}${ChatColor.RED}${game.gameEnemyStats[entity]?.name}의 $effectText 효과가 ${turnTime}턴 동안 적용됩니다.")
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

        sendMessage("${ChatColor.BOLD}${ChatColor.GREEN}$effectText 효과가 ${turnTime}턴 후 제거됩니다.", "${ChatColor.BOLD}${ChatColor.RED}${game.gameEnemyStats[entity]?.name}의 $effectText 효과가 ${turnTime}턴 후 제거됩니다.")
    }

    fun countEnd() {
        val game = Info().getGame() ?: return

        if (!entity.isDead) {
            sendMessage("${ChatColor.BOLD}${ChatColor.GREEN}$effectText 효과가 제거되었습니다.", "${ChatColor.BOLD}${ChatColor.RED}${game.gameEnemyStats[entity]?.name}의 $effectText 효과가 제거되었습니다.")
            applyEndEffect(game)
        }

        game.countTimer.remove(this)
    }

    private fun sendMessage(playerMessage: String, enemyMessage: String) {
        if (entity is Player) {
            entity.sendMessage(playerMessage)
        } else {
            TextManager().gameNotification(enemyMessage)
        }
    }

    private fun applyEndEffect(game: Game) {
        when (this.tagString) {
            "WolfDeadSpeedUpTag" -> {
                game.gameEnemyStats[entity]?.relativeSpeed?.minus(1)
            }
        }
    }
}