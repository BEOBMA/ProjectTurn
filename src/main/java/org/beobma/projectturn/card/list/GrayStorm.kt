package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.attack
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.targetingFailSound
import org.beobma.projectturn.text.TextManager.Companion.targetingFailText
import org.beobma.projectturn.util.Utill
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GrayStorm  {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        val target = Utill().shootLaser(player)

        if (target == null || target.isTeam()) {
            player.sendMessage(targetingFailText())
            player.targetingFailSound()
            return
        }
        else {
            game.gamePlayerStats[player]?.useCard(item, Card("잿빛 폭풍", listOf("${ChatColor.GRAY}바라보는 적이 보유한 ${burnText()} 만큼 모든 적에게 부여합니다."), RarityType.Common, 1))
            Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) //사운드 수정

            val fire = game.gameEnemyStats[target]?.getBurn() ?: return

            if (fire <= 0) {
                return
            }

            game.gameEnemy.forEach {
                game.gameEnemyStats[it]?.addBurn(fire, player)
            }
        }
    }
}