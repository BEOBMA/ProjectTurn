package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.manaText
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BreathOfHeatwave {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        game.gamePlayerStats[player]?.useCard(item, Card("폭염의 숨결", listOf("${manaText()}를 2 회복하고 ${burnText()} 1을 얻습니다."), RarityType.Common, 0))
        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F) // 사운드 수정
        game.gamePlayerStats[player]?.addMana(2)
        game.gamePlayerStats[player]?.addBurn(1, game.gameEnemy.random())
    }
}