package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.util.Utill
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Uranus {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return

        game.gamePlayerStats[player]?.useCard(
            item, Card("천왕성", listOf("${ChatColor.GRAY}자신의 체력을 4 만큼 회복합니다."), RarityType.Uncommon, 2)
        )
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) // 수정 필요

        game.gamePlayerStats[player]!!.heal(4, player)
    }
}