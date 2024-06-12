package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Earth {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return

        game.gamePlayerStats[player]?.useCard(
            item,
            Card("지구", listOf("${ChatColor.GRAY}8의 방어력을 얻고 카드를 1장 뽑습니다."), RarityType.Common, 1)
        )
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) // 수정 필요
        game.gamePlayerStats[player]?.cardDraw(1)
        game.gamePlayerStats[player]?.addDefense(8)
    }
}