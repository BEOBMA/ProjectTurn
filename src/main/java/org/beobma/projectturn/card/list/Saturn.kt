package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.util.Utill
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Saturn {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return

        game.gamePlayerStats[player]?.useCard(
            item, Card("토성", listOf("${ChatColor.GRAY}묘지의 카드중 무작위로 2장을 패에 넣습니다."), RarityType.Uncommon, 1)
        )
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) // 수정 필요
        if (game.gamePlayerStats[player]!!.cemetry.size <= 0) {
            return
        }
        val card = game.gamePlayerStats[player]!!.cemetry.random()
        game.gamePlayerStats[player]!!.cemetry.remove(card)
        game.gamePlayerStats[player]!!.addCard(card)

        if (game.gamePlayerStats[player]!!.cemetry.size <= 0) {
            return
        }

        val card1 = game.gamePlayerStats[player]!!.cemetry.random()
        game.gamePlayerStats[player]!!.cemetry.remove(card1)
        game.gamePlayerStats[player]!!.addCard(card1)
    }
}