package org.beobma.projectturn.card.list

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.DevelopersPowers
import org.beobma.projectturn.text.TextManager.Companion.targetingFailSound
import org.beobma.projectturn.text.TextManager.Companion.targetingFailText
import org.beobma.projectturn.util.Utill
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class DevelopersPowers {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        val target = Utill().shootLaser(player)

        if (target == null || target.isTeam()) {
            player.sendMessage(targetingFailText())
            player.targetingFailSound()
            return
        }
        else {
            game.gamePlayerStats[player]?.useCard(item, DevelopersPowers, false)
            game.gamePlayerStats[player]?.addCard(DevelopersPowers)
            game.gameEnemyStats[target]?.death()
        }
    }
}